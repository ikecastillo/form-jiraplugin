export interface LayoutPosition {
  row: number;
  column: number;
  span: number;
}

export interface ComponentInstanceConfig {
  instanceId: string;
  componentId: string;
  position: LayoutPosition;
  configuration: Record<string, unknown>;
  permissions?: string[];
  enabled: boolean;
  sortOrder: number;
}

export interface BrandingConfig {
  logoUrl?: string;
  primaryColor: string;
  secondaryColor?: string;
  customCSS?: string;
}

export interface LayoutConfig {
  type: string;
  columns: number;
  gap?: string;
  maxWidth?: string;
}

export interface PortalPermissionsConfig {
  allowedGroups?: string[];
  adminGroups?: string[];
  publicAccess: boolean;
}

export interface JiraIntegrationConfig {
  serviceDeskId: string;
  defaultRequestTypeId?: string;
  fieldMappings: Record<string, string>;
}

export interface PortalConfiguration {
  portalId: string;
  name: string;
  description?: string;
  branding: BrandingConfig;
  layout: LayoutConfig;
  components: ComponentInstanceConfig[];
  permissions: PortalPermissionsConfig;
  jiraIntegration: JiraIntegrationConfig;
  customSettings?: Record<string, unknown>;
}

export interface RequestType {
  id: string;
  name: string;
  [key: string]: unknown;
}

export interface RequestTypeField {
  fieldId: string;
  name: string;
  required: boolean;
  [key: string]: unknown;
}

export interface ServiceRequestData {
  serviceDeskId: string;
  requestTypeId: string;
  formData: Record<string, unknown>;
  requestParticipants?: string[];
}

class HRPortalAPI {
  private readonly baseURL: string;

  constructor() {
    this.baseURL = '/plugins/servlet/hr-portal/api';
  }

  private async makeRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${this.baseURL}${endpoint}` as const, {
      ...options,
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        'X-Atlassian-Token': 'nocheck',
        ...options.headers,
      },
      credentials: 'same-origin',
    });

    if (!response.ok) {
      let errorMessage = `${response.status} ${response.statusText}`;
      try {
        const errorData = await response.json();
        errorMessage = (errorData?.error as string) ?? errorMessage;
      } catch (error) {
        // Ignore JSON parse errors and use default message
      }
      throw new Error(errorMessage);
    }

    if (response.status === 204) {
      // No content
      return undefined as T;
    }

    return (await response.json()) as T;
  }

  async getPortalConfiguration(portalId: string): Promise<PortalConfiguration> {
    return this.makeRequest<PortalConfiguration>(`/portals/${encodeURIComponent(portalId)}/config`);
  }

  async updatePortalConfiguration(portalId: string, config: PortalConfiguration): Promise<void> {
    await this.makeRequest(`/portals/${encodeURIComponent(portalId)}/config`, {
      method: 'PUT',
      body: JSON.stringify(config),
    });
  }

  async getRequestTypes(serviceDeskId: string, search?: string): Promise<RequestType[]> {
    const params = new URLSearchParams();
    if (search) {
      params.append('search', search);
    }
    const query = params.toString();
    const suffix = query ? `?${query}` : '';
    return this.makeRequest<RequestType[]>(`/jira/service-desks/${encodeURIComponent(serviceDeskId)}/request-types${suffix}`);
  }

  async createServiceRequest(requestData: ServiceRequestData): Promise<{ issueKey: string }> {
    return this.makeRequest<{ issueKey: string }>('/jira/requests', {
      method: 'POST',
      body: JSON.stringify(requestData),
    });
  }

  async getRequestTypeFields(serviceDeskId: string, requestTypeId: string): Promise<RequestTypeField[]> {
    return this.makeRequest<RequestTypeField[]>(
      `/jira/service-desks/${encodeURIComponent(serviceDeskId)}/request-types/${encodeURIComponent(requestTypeId)}/fields`,
    );
  }
}

export const hrPortalAPI = new HRPortalAPI();
