# Portal Admin & Configuration Pages - Project Skeleton

## Overview
This document outlines the complete file structure for implementing the "final boss" portal administration and configuration system. This includes both backend APIs and frontend admin interfaces for managing portals, components, and configurations.

---

## Backend File Structure

### 📁 `backend/src/main/java/com/switchhr/jsm/`

#### Admin Controllers & REST APIs
```
admin/
├── AdminPortalController.java          # Main portal CRUD operations
├── AdminComponentController.java       # Component management APIs
├── AdminConfigurationController.java   # Configuration management
├── AdminPermissionController.java      # User/group permission management
├── AdminAuditController.java          # Audit log viewing and management
├── AdminDashboardController.java      # Admin dashboard metrics and stats
└── AdminExportController.java         # Export/import portal configurations
```

#### Admin Services
```
admin/service/
├── AdminPortalService.java            # Portal management business logic
├── AdminComponentRegistryService.java # Component registration and management
├── AdminPermissionService.java        # Permission management logic
├── AdminValidationService.java        # Admin-specific validation rules
├── AdminExportImportService.java      # Configuration export/import
├── AdminAuditService.java             # Audit logging and retrieval
└── AdminNotificationService.java      # Admin notifications and alerts
```

#### Configuration Management
```
config/admin/
├── AdminPortalConfigDTO.java          # Admin portal configuration model
├── ComponentConfigurationDTO.java     # Component configuration model
├── PortalTemplateDTO.java             # Portal template definitions
├── ComponentLibraryDTO.java           # Component library management
├── PermissionConfigDTO.java           # Permission configuration model
├── AdminUserPreferencesDTO.java       # Admin user preferences
└── SystemConfigurationDTO.java        # System-wide settings
```

#### Security & Permissions
```
security/admin/
├── AdminSecurityFilter.java           # Admin-only access filter
├── AdminPermissionChecker.java        # Admin permission validation
├── AdminRoleValidator.java            # Role-based access validation
└── AdminSessionManager.java           # Admin session management
```

#### Models & Entities
```
model/admin/
├── AdminUser.java                     # Admin user entity (ActiveObjects)
├── PortalTemplate.java                # Portal template entity
├── ComponentLibrary.java              # Component library entity
├── AdminAuditLog.java                 # Admin action audit log
├── SystemConfiguration.java           # System configuration entity
└── AdminNotification.java             # Admin notification entity
```

#### Validation & Utils
```
validation/admin/
├── AdminPortalValidator.java          # Portal configuration validation
├── AdminComponentValidator.java       # Component configuration validation
├── AdminPermissionValidator.java      # Permission validation
└── AdminConfigurationValidator.java   # General admin config validation

utils/admin/
├── AdminExportUtils.java              # Configuration export utilities
├── AdminImportUtils.java              # Configuration import utilities
├── AdminReportGenerator.java          # Admin report generation
└── AdminBackupManager.java            # Configuration backup management
```

---

## Frontend File Structure

### 📁 `frontend/`

#### Admin Application Entry
```
admin/
├── layout.tsx                         # Admin layout wrapper
├── page.tsx                          # Admin dashboard home page
├── loading.tsx                       # Admin loading states
├── error.tsx                         # Admin error boundaries
└── not-found.tsx                     # Admin 404 page
```

#### Portal Management Pages
```
admin/portals/
├── page.tsx                          # Portal list/overview page
├── create/
│   └── page.tsx                      # Create new portal page
├── [portalId]/
│   ├── page.tsx                      # Portal details/edit page
│   ├── components/
│   │   └── page.tsx                  # Portal component management
│   ├── permissions/
│   │   └── page.tsx                  # Portal permission management
│   ├── settings/
│   │   └── page.tsx                  # Portal settings page
│   ├── preview/
│   │   └── page.tsx                  # Portal preview page
│   └── audit/
│       └── page.tsx                  # Portal audit log
├── templates/
│   ├── page.tsx                      # Portal template library
│   └── [templateId]/
│       └── page.tsx                  # Template details/edit
└── import-export/
    └── page.tsx                      # Portal import/export page
```

#### Component Management Pages
```
admin/components/
├── page.tsx                          # Component library overview
├── create/
│   └── page.tsx                      # Create custom component
├── [componentId]/
│   ├── page.tsx                      # Component details/edit
│   ├── configuration/
│   │   └── page.tsx                  # Component configuration schema
│   ├── preview/
│   │   └── page.tsx                  # Component preview
│   └── usage/
│       └── page.tsx                  # Component usage analytics
├── marketplace/
│   └── page.tsx                      # Component marketplace/library
└── import/
    └── page.tsx                      # Import external components
```

#### System Configuration Pages
```
admin/system/
├── page.tsx                          # System overview dashboard
├── settings/
│   └── page.tsx                      # Global system settings
├── integrations/
│   ├── page.tsx                      # Integration overview
│   ├── jira/
│   │   └── page.tsx                  # Jira integration settings
│   └── external/
│       └── page.tsx                  # External API integrations
├── security/
│   ├── page.tsx                      # Security settings overview
│   ├── permissions/
│   │   └── page.tsx                  # Global permission management
│   └── audit/
│       └── page.tsx                  # System audit logs
└── maintenance/
    ├── page.tsx                      # System maintenance tools
    ├── backup/
    │   └── page.tsx                  # Backup management
    └── cleanup/
        └── page.tsx                  # Data cleanup tools
```

#### User & Permission Management
```
admin/users/
├── page.tsx                          # User management overview
├── [userId]/
│   ├── page.tsx                      # User details/edit
│   ├── permissions/
│   │   └── page.tsx                  # User permission management
│   └── activity/
│       └── page.tsx                  # User activity log
├── groups/
│   ├── page.tsx                      # Group management
│   └── [groupId]/
│       └── page.tsx                  # Group details/edit
└── roles/
    ├── page.tsx                      # Role management
    └── [roleId]/
        └── page.tsx                  # Role details/edit
```

#### Analytics & Reporting
```
admin/analytics/
├── page.tsx                          # Analytics dashboard
├── portals/
│   └── page.tsx                      # Portal usage analytics
├── components/
│   └── page.tsx                      # Component usage analytics
├── users/
│   └── page.tsx                      # User activity analytics
├── performance/
│   └── page.tsx                      # Performance metrics
└── reports/
    ├── page.tsx                      # Report generator
    └── [reportId]/
        └── page.tsx                  # Report details/view
```

#### Admin Components Library
```
components/admin/
├── layout/
│   ├── AdminSidebar.tsx              # Admin navigation sidebar
│   ├── AdminHeader.tsx               # Admin header with user menu
│   ├── AdminBreadcrumbs.tsx          # Navigation breadcrumbs
│   └── AdminFooter.tsx               # Admin footer
├── forms/
│   ├── PortalConfigForm.tsx          # Portal configuration form
│   ├── ComponentConfigForm.tsx       # Component configuration form
│   ├── PermissionForm.tsx            # Permission management form
│   ├── UserForm.tsx                  # User creation/edit form
│   └── GroupForm.tsx                 # Group creation/edit form
├── tables/
│   ├── PortalTable.tsx               # Portal listing table
│   ├── ComponentTable.tsx            # Component listing table
│   ├── UserTable.tsx                 # User management table
│   ├── AuditTable.tsx                # Audit log table
│   └── AnalyticsTable.tsx            # Analytics data table
├── editors/
│   ├── JSONConfigEditor.tsx          # JSON configuration editor
│   ├── ComponentEditor.tsx           # Visual component editor
│   ├── PermissionEditor.tsx          # Permission editor interface
│   └── TemplateEditor.tsx            # Portal template editor
├── viewers/
│   ├── PortalPreview.tsx             # Portal preview component
│   ├── ComponentPreview.tsx          # Component preview
│   ├── ConfigurationViewer.tsx       # Configuration display
│   └── AuditViewer.tsx               # Audit log viewer
├── wizards/
│   ├── PortalCreationWizard.tsx      # Multi-step portal creation
│   ├── ComponentImportWizard.tsx     # Component import wizard
│   └── PermissionSetupWizard.tsx     # Permission setup wizard
├── dashboards/
│   ├── AdminDashboard.tsx            # Main admin dashboard
│   ├── PortalDashboard.tsx           # Portal-specific dashboard
│   ├── SystemHealthDashboard.tsx     # System health metrics
│   └── AnalyticsDashboard.tsx        # Analytics dashboard
└── modals/
    ├── DeleteConfirmModal.tsx        # Delete confirmation dialog
    ├── ImportModal.tsx               # Import configuration modal
    ├── ExportModal.tsx               # Export configuration modal
    ├── PermissionModal.tsx           # Permission assignment modal
    └── NotificationModal.tsx         # Admin notification modal
```

#### Admin UI Components
```
components/admin/ui/
├── AdminButton.tsx                   # Admin-styled buttons
├── AdminCard.tsx                     # Admin dashboard cards
├── AdminTabs.tsx                     # Admin tabbed interfaces
├── AdminModal.tsx                    # Admin modal dialogs
├── AdminTooltip.tsx                  # Admin tooltips
├── AdminAlert.tsx                    # Admin alert messages
├── AdminBadge.tsx                    # Status badges
├── AdminProgress.tsx                 # Progress indicators
├── AdminSkeleton.tsx                 # Loading skeletons
└── AdminDataTable.tsx                # Enhanced data tables
```

#### Admin Hooks & Utilities
```
lib/admin/
├── hooks/
│   ├── useAdminAuth.ts               # Admin authentication hook
│   ├── usePortalManagement.ts        # Portal management hooks
│   ├── useComponentRegistry.ts       # Component registry hooks
│   ├── usePermissions.ts             # Permission management hooks
│   ├── useAuditLog.ts                # Audit log hooks
│   ├── useAnalytics.ts               # Analytics data hooks
│   └── useSystemHealth.ts            # System health monitoring
├── api/
│   ├── admin-api.ts                  # Admin API client
│   ├── portal-api.ts                 # Portal management API
│   ├── component-api.ts              # Component management API
│   ├── permission-api.ts             # Permission management API
│   ├── audit-api.ts                  # Audit log API
│   └── analytics-api.ts              # Analytics API
├── utils/
│   ├── admin-helpers.ts              # Admin utility functions
│   ├── config-validator.ts           # Configuration validation
│   ├── export-helpers.ts             # Export utility functions
│   ├── import-helpers.ts             # Import utility functions
│   └── permission-helpers.ts         # Permission utility functions
├── types/
│   ├── admin-types.ts                # Admin TypeScript types
│   ├── portal-types.ts               # Portal-related types
│   ├── component-types.ts            # Component-related types
│   ├── permission-types.ts           # Permission-related types
│   └── analytics-types.ts            # Analytics-related types
└── constants/
    ├── admin-constants.ts            # Admin constants
    ├── permission-constants.ts       # Permission constants
    └── config-constants.ts           # Configuration constants
```

#### Admin Styles & Themes
```
styles/admin/
├── admin-globals.css                 # Admin-specific global styles
├── admin-components.css              # Admin component styles
├── admin-layout.css                  # Admin layout styles
├── admin-forms.css                   # Admin form styles
├── admin-tables.css                  # Admin table styles
└── admin-themes.css                  # Admin color themes
```

---

## Configuration Files

### JSON Schema Definitions
```
backend/src/main/resources/schemas/admin/
├── portal-configuration-schema.json     # Portal config JSON schema
├── component-configuration-schema.json  # Component config JSON schema
├── permission-configuration-schema.json # Permission config JSON schema
├── system-configuration-schema.json     # System config JSON schema
└── template-schema.json                 # Portal template JSON schema
```

### Admin Templates
```
backend/src/main/resources/templates/admin/
├── admin-portal.vm                   # Admin portal template
├── admin-dashboard.vm                # Admin dashboard template
└── admin-error.vm                    # Admin error page template
```

### Admin Properties
```
backend/src/main/resources/admin/
├── admin-plugin.properties           # Admin-specific properties
├── admin-permissions.properties      # Permission definitions
└── admin-messages.properties         # Admin UI messages
```

---

## Key Page Descriptions

### 🏠 **Admin Dashboard** (`admin/page.tsx`)
- System overview with key metrics
- Quick access to recent portals and components
- System health indicators
- Recent admin activities

### 🎯 **Portal Management** (`admin/portals/page.tsx`)
- List all portals with status indicators
- Search, filter, and sort portals
- Quick actions (edit, clone, delete, preview)
- Portal usage statistics

### 🧩 **Component Library** (`admin/components/page.tsx`)
- Browse all available components
- Component categories and search
- Usage analytics for each component
- Component installation status

### ⚙️ **Portal Configuration** (`admin/portals/[portalId]/page.tsx`)
- Visual portal builder interface
- Drag-and-drop component placement
- JSON configuration editor
- Real-time preview panel

### 👥 **Permission Management** (`admin/portals/[portalId]/permissions/page.tsx`)
- User and group access controls
- Role-based permission assignment
- Permission inheritance visualization
- Bulk permission operations

### 📊 **Analytics Dashboard** (`admin/analytics/page.tsx`)
- Portal usage metrics
- Component popularity statistics
- User engagement analytics
- Performance monitoring

### 🔧 **System Settings** (`admin/system/page.tsx`)
- Global system configuration
- Integration settings
- Security policies
- Maintenance tools

---

## User Experience Flow

### Portal Creation Workflow:
1. **Template Selection** → Choose from templates or start blank
2. **Basic Configuration** → Name, description, branding
3. **Component Assembly** → Drag-and-drop interface
4. **Permission Setup** → User/group access configuration
5. **Preview & Test** → Real-time portal preview
6. **Publish** → Make portal live

### Component Management Workflow:
1. **Component Library** → Browse available components
2. **Configuration** → Set component parameters
3. **Styling** → Apply custom styles
4. **Testing** → Component preview and validation
5. **Deployment** → Publish to component library

### Permission Management Workflow:
1. **User Selection** → Choose users/groups
2. **Role Assignment** → Assign appropriate roles
3. **Portal Access** → Configure portal-specific permissions
4. **Review** → Permission summary and validation
5. **Apply** → Activate permission changes

---

## Technical Considerations

### State Management:
- **React Query** for server state management
- **Zustand** for client-side admin state
- **Context API** for admin user context

### Form Handling:
- **React Hook Form** with Zod validation
- **Dynamic form generation** from JSON schemas
- **Auto-save functionality** for configuration changes

### Real-time Features:
- **WebSocket connections** for live portal previews
- **Server-sent events** for admin notifications
- **Real-time collaboration** on portal editing

### Performance:
- **Virtual scrolling** for large component lists
- **Lazy loading** for portal previews
- **Debounced search** and filtering
- **Optimistic updates** for better UX

---

## Security Features

### Access Control:
- **Multi-factor authentication** for admin access
- **Role-based permissions** with inheritance
- **Session management** with timeout
- **Activity logging** for all admin actions

### Data Protection:
- **Configuration encryption** for sensitive data
- **Backup and recovery** systems
- **Version control** for configuration changes
- **Audit trails** for compliance

---

This skeleton provides the complete structure for building a comprehensive portal administration system that allows non-technical users to create, configure, and manage portals through an intuitive interface while maintaining the flexibility and power needed for enterprise deployments.