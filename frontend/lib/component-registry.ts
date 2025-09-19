import React from "react";

export type ComponentType = "form" | "content" | "layout" | "support";

export interface ComponentDefinition {
  id: string;
  name: string;
  type: ComponentType;
  component: React.LazyExoticComponent<React.ComponentType<any>>;
  defaultConfig: Record<string, unknown>;
  configSchema?: Record<string, unknown>;
}

class ComponentRegistry {
  private static instance: ComponentRegistry;
  private components = new Map<string, ComponentDefinition>();
  private initialized = false;

  static getInstance(): ComponentRegistry {
    if (!ComponentRegistry.instance) {
      ComponentRegistry.instance = new ComponentRegistry();
    }
    return ComponentRegistry.instance;
  }

  registerComponent(definition: ComponentDefinition): void {
    this.components.set(definition.id, definition);
  }

  getComponent(id: string): ComponentDefinition | undefined {
    if (!this.initialized) {
      this.initializeDefaultComponents();
    }
    return this.components.get(id);
  }

  getAll(): ComponentDefinition[] {
    if (!this.initialized) {
      this.initializeDefaultComponents();
    }
    return Array.from(this.components.values());
  }

  initializeDefaultComponents(): void {
    if (this.initialized) {
      return;
    }

    this.registerComponent({
      id: "form-selector",
      name: "Form Selector",
      type: "form",
      component: React.lazy(() =>
        import("../components/form-selector").then((module) => ({ default: module.FormSelector })),
      ),
      defaultConfig: {
        title: "Select a Form",
        description: "Choose the appropriate HR form for your request.",
      },
    });

    this.registerComponent({
      id: "transfer-promotion-form",
      name: "Transfer & Promotion Form",
      type: "form",
      component: React.lazy(() => import("../app/transfer-promotion/page")),
      defaultConfig: {
        enableValidation: true,
      },
    });

    this.registerComponent({
      id: "link-group",
      name: "Link Group",
      type: "content",
      component: React.lazy(() => import("../components/link-group")),
      defaultConfig: {
        title: "Quick Links",
        links: [],
      },
    });

    this.initialized = true;
  }
}

export const componentRegistry = ComponentRegistry.getInstance();
