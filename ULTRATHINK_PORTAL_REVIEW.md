# HR Portal Plugin – Technical Deep Dive & Jira Integration Plan

## 1. Current Architecture Snapshot

### Backend (Data Center Plugin)
- **Tech stack**: Java 17, Atlassian SDK AMPS 9.1.1, Spring Scanner 2.2.4, Servlet 3.1 (`pom.xml`).
- **Primary entry**: Servlet at `/hr-portal` renders a Velocity template with React mount point (`backend/src/main/java/com/switchhr/jsm/servlet/HRPortalServlet.java:1`, `backend/src/main/resources/templates/hr-portal.vm:1`).
- **Web resources**: Packaged JS/CSS/PNG under `com.switchhr.jsm.hrportal:hr-portal-resources` loaded via `$webResourceManager` (`backend/src/main/resources/atlassian-plugin.xml:1`).
- **Services in use**: `TemplateRenderer`, `ApplicationProperties` for base URL resolution; no REST modules, filters, or persistence yet.

### Frontend (Portal UI Bundle)
- **Framework**: Next.js 15 (App Router) + React 19, transpiled to a static bundle via esbuild for Jira (`frontend/package.json`).
- **Styling**: Tailwind CSS v4 with inline theme tokens matching Switch branding (`frontend/app/globals.css`). Radix UI primitives + shadcn wrappers for consistent controls (`frontend/components/ui/*`).
- **Key components**: Form selector landing (`frontend/components/form-selector.tsx`), multi-step Transfer & Promotion form (`frontend/app/transfer-promotion/page.tsx`).
- **Bootstrap**: `plugin-entry.tsx` exposes `window.initializeHRPortalApp` and hydrates the React tree into `#hr-portal-root` (`frontend/plugin-entry.tsx`).

### Build & Packaging
- **Pipeline**: Maven runs `frontend-maven-plugin` → installs Node 20.12, executes `npm ci`, runs `npm run build:plugin` (`backend/pom.xml`).
- **Build script**: `scripts/build-plugin.js` triggers Tailwind CLI for CSS and esbuild for JS, then copies outputs into backend resources (`frontend/scripts/build-plugin.js`).
- **Hot reload**: AMPS quick reload enabled; no watch integration between Next dev and plugin yet.

## 2. Runtime Flow Today
1. User navigates to `/plugins/servlet/hr-portal` (or mapped web item) hitting `HRPortalServlet`.
2. Servlet renders `hr-portal.vm`, which requires the web-resource, injects styles, scripts, and seeds bootstrap context (`baseUrl`, `resourceBase`, mount ID).
3. Bundled JS registers `initializeHRPortalApp` and immediately mounts `PortalApp`, rendering the form selector or the static multi-step flow.
4. All interactions are client-side only; no calls back to Jira APIs or plugin services.

## 3. Jira Integration Touchpoints (Current vs Desired)
- **Present**: Only uses Jira base URL for asset resolution; the UI is isolated from Jira data and permissions beyond servlet-level access.
- **Target**:
  - Fetch Service Management request types per project/portal.
  - Launch native Jira create experience (iframe/dialog) for selected request type.
  - Enforce platform auth/permissions and optionally impersonate project context.
  - Offer lightweight admin config under Project settings to toggle UI modules and link destinations without hand-editing code.

## 4. Extending Jira Integration Safely

### 4.1 Request Type Catalogue API
- Add a Jersey REST module (e.g., `@Path("/portal")`) that the React bundle can call.
- Inside, use `ServiceDeskManager` + `RequestTypeService` to list request types for the active Service Desk project. Wrap in permission checks via `PermissionManager` to ensure the caller can view the queue.
- Cache responses per project/user with a short TTL using `com.atlassian.cache.CacheManager` to avoid hammering the JSM APIs.
- Wire the front end to request `/rest/hrportal/1.0/portal/{portalId}/request-types` on load; hydrate the selector grid dynamically instead of the current static array.

### 4.2 Create Screen Dialog Integration
- Use Jira’s dialog pattern: the plugin can render a lightweight AUI dialog or React modal that hosts an `<iframe>` pointing to `/servicedesk/customer/portal/{portalId}/group/{groupId}/create/{requestTypeId}`.
- Store the request-type metadata provided by the REST endpoint (name, description, request type key, create URL). When a user clicks a tile, open the iframe dialog with `AP.dialog.create` (for Connect) or custom React modal to preserve branding while staying native.
- Provide fallback logic: if the iframe cannot load (permissions, network), surface the direct link with `target="_blank"` to keep parity with Jira behaviors.

### 4.3 Data Contracts & Validation
- Normalize REST payloads to simple DTOs (id, label, summary, icon hint). Avoid leaking Jira’s internal model to the client so the UI can remain stylistically consistent.
- Apply server-side validation on any form submissions that eventually create Jira issues (see §6.2) to prevent injection or missing required fields.

## 5. UI/UX Strategy – Configurable Without Going “Too Custom”

### 5.1 Component Library Discipline
- Keep using Radix + shadcn wrappers to ensure accessibility and maintainable styling. Favor utility classes already defined in `app/globals.css` to keep the brand theme centralized.
- Introduce a `components/registry.ts` that maps config keys to React components (e.g., `hero`, `tileGrid`, `announcement`). The page can compose blocks based on configuration supplied by the backend.

### 5.2 Data-Driven Layout
- Model portal pages as JSON documents: `{ hero: {...}, tiles: [...], footerLinks: [...] }` stored per Jira project (see §6.3).
- Render puzzle pieces by iterating over that JSON rather than hard-coding markup. Each piece should have sensible defaults so a new portal works out-of-the-box without customization.
- Allow optional CTA links/buttons with predefined style variants (primary, secondary, inline) to discourage arbitrary HTML injection while giving admins flexibility.

### 5.3 Style Guardrails
- Continue using the Velocity template with `decorator none` for a clean canvas, but consider including Atlassian’s `aui` typography classes for better Jira visual harmony.
- Keep brand tokens in one place (`app/globals.css`) and expose a minimal set of override slots (e.g., accent color, footer text). Expose these as fields in the upcoming Project settings UI; validate to hex or text to prevent layout-breaking input.

## 6. Backend Enhancements

### 6.1 Authentication Filter
- Register a servlet filter to guarantee authenticated access:
  ```xml
  <servlet-filter key="hr-portal-auth-filter" class="com.switchhr.jsm.filter.HRPortalAuthFilter">
      <url-pattern>/plugins/servlet/hr-portal</url-pattern>
      <dispatcher>REQUEST</dispatcher>
      <dispatcher>FORWARD</dispatcher>
  </servlet-filter>
  ```
- Implementation sketch:
  ```java
  @Scanned
  public class HRPortalAuthFilter implements Filter {
      private final LoginUriProvider loginUriProvider;
      private final JiraAuthenticationContext authContext;

      @Inject
      public HRPortalAuthFilter(LoginUriProvider loginUriProvider, JiraAuthenticationContext authContext) {
          this.loginUriProvider = loginUriProvider;
          this.authContext = authContext;
      }

      public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
          HttpServletRequest request = (HttpServletRequest) req;
          HttpServletResponse response = (HttpServletResponse) res;
          if (authContext.getLoggedInUser() == null) {
              response.sendRedirect(loginUriProvider.getLoginUri(request).toASCIIString());
              return;
          }
          chain.doFilter(req, res);
      }
  }
  ```
- Co-locate the class in `backend/src/main/java/com/switchhr/jsm/filter/` and ensure Spring Scanner picks it up.

### 6.2 REST + Form Submission
- Add a REST resource to accept form submissions, map them to Jira issue create (`IssueService`) or to Service Management request creation using `CustomerRequestCreateParameters`.
- Apply permission checks: confirm the logged-in user has `CREATE_ISSUES` or is an agent/customer with access to the portal.
- Return validation errors with clear messages for the React form; hook the existing toast notifications to display status.

### 6.3 Project Settings Integration
- Provide an admin UI module (e.g., `project-config-tabpanel`) scoped to Service Management projects.
- Store configuration using `PluginSettingsFactory` or `ActiveObjects` for richer data. Example schema per project: theme overrides, tile ordering, custom footer, feature toggles.
- Expose REST endpoints for CRUD operations on that configuration; reuse them in both the Project settings screen and the runtime portal load to keep behavior consistent.

## 7. Security & Permissions Checklist
- Enforce authentication via the new filter (§6.1) and double-check REST resources with `@AnonymousAllowed` omitted.
- Sanitize any user-provided strings before rendering (Velocity already escapes with `$textutil.htmlEncode` if used; React auto-escapes by default).
- For iframe dialogs, set appropriate headers (`X-Frame-Options` already handled by Jira) and consider CSP adjustments via plugin `web-resource` if additional origins are needed.
- Respect Service Management customer vs agent context; do not expose agent-only request types to customers.
- Log key events (failed auth, API failures) using Atlassian’s `AuditLogService` for traceability.

## 8. Operational & Testing Considerations
- **Testing**: add Jest or Vitest for UI unit tests; use Cypress component tests for form flows. For backend, add integration tests with AMPS’ `@RestoreData` harness where feasible.
- **CI/CD**: ensure `npm run build:plugin` runs during Maven verify; cache `node/node_modules` between builds to speed up pipelines.
- **Performance**: bundle is currently ~1 iife; monitor size after adding dynamic modules. Consider code-splitting request-type dialog if it grows.
- **Localization**: current strings partly hard-coded in React; migrate to i18n bundle + REST provided text for consistency with Jira locales.

## 9. Suggested Roadmap
1. **Foundations**: Implement auth filter, add REST scaffolding, move static form data into typed service layer.
2. **Dynamic Data**: Integrate request-type fetch + dialog launch, replace hard-coded `formOptions` with REST-backed data.
3. **Configurability**: Build Project settings screen + config persistence; render page from configuration schema.
4. **Submission Flow**: Wire form submission to Jira issue/request creation with validation and error handling.
5. **Polish**: Add localization, analytics hooks, and refine theming knobs without breaking alignment with Jira styling.

---
*This document covers the current snapshot (March 2025) of the HR Portal plugin codebase alongside recommended steps to deepen Jira integration while preserving the existing branded experience.*
