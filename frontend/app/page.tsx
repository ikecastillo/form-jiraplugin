"use client";

import { Suspense, useEffect, useMemo, useState } from "react";
import { QueryClient, QueryClientProvider, useQuery } from "@tanstack/react-query";
import { hrPortalAPI, PortalConfiguration, ComponentInstanceConfig } from "@/lib/api";
import { componentRegistry } from "@/lib/component-registry";
import { FormSelector, FormSelectorConfig, FormId } from "@/components/form-selector";
import TransferPromotionForm from "./transfer-promotion/page";

function LoadingState() {
  return (
    <div className="w-full py-10 text-center text-muted-foreground">
      Loading portal configuration...
    </div>
  );
}

function ErrorState({ message }: { message: string }) {
  return (
    <div className="w-full py-10 text-center text-destructive">
      {message}
    </div>
  );
}

type PortalAppProps = {
  assetBase?: string;
  portalId?: string;
};

function PortalContent({ assetBase = "", portalId }: Required<PortalAppProps>) {
  const [activeForm, setActiveForm] = useState<FormId | null>(null);

  useEffect(() => {
    componentRegistry.initializeDefaultComponents();
  }, []);

  const { data, isLoading, isError, error } = useQuery<PortalConfiguration>({
    queryKey: ["portal-configuration", portalId],
    queryFn: () => hrPortalAPI.getPortalConfiguration(portalId),
  });

  const sortedComponents = useMemo(() => {
    if (!data?.components) {
      return [] as ComponentInstanceConfig[];
    }
    return [...data.components]
      .filter((component) => component.enabled !== false)
      .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0));
  }, [data?.components]);

  if (isLoading) {
    return <LoadingState />;
  }

  if (isError || !data) {
    return <ErrorState message={error instanceof Error ? error.message : "Unable to load portal"} />;
  }

  const handleSelect = (formId: FormId) => {
    setActiveForm(formId);
  };

  const handleBack = () => {
    setActiveForm(null);
  };

  return (
    <div className="space-y-8">
      {sortedComponents.map((instance) => {
        const { componentId, configuration } = instance;

        if (componentId === "form-selector") {
          if (activeForm) {
            return null;
          }
          return (
            <FormSelector
              key={instance.instanceId}
              assetBase={assetBase}
              config={configuration as FormSelectorConfig}
              onSelect={handleSelect}
            />
          );
        }

        if (componentId === "transfer-promotion-form") {
          if (activeForm !== "transfer-promotion") {
            return null;
          }
          return (
            <TransferPromotionForm key={instance.instanceId} assetBase={assetBase} onBack={handleBack} />
          );
        }

        const definition = componentRegistry.getComponent(componentId);
        if (!definition) {
          return (
            <ErrorState
              key={instance.instanceId}
              message={`Component \"${componentId}\" is not registered.`}
            />
          );
        }

        const DynamicComponent = definition.component;
        return (
          <Suspense
            key={instance.instanceId}
            fallback={<LoadingState />}
          >
            <DynamicComponent config={configuration} assetBase={assetBase} />
          </Suspense>
        );
      })}

      {!sortedComponents.some((component) => component.componentId === "form-selector") && !activeForm ? (
        <ErrorState message="Portal configuration does not include a form selector." />
      ) : null}
    </div>
  );
}

const queryClient = new QueryClient();

export default function PortalApp({ assetBase = "", portalId = "hr-portal" }: PortalAppProps) {
  const resolvedPortalId = portalId || "default";

  return (
    <QueryClientProvider client={queryClient}>
      <PortalContent assetBase={assetBase} portalId={resolvedPortalId} />
    </QueryClientProvider>
  );
}
