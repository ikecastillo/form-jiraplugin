"use client";

import { useState } from "react";
import { FormSelector } from "@/components/form-selector";
import TransferPromotionForm from "./transfer-promotion/page";

type PortalView = "form-selector" | "transfer-promotion";

type PortalAppProps = {
  assetBase?: string;
};

export default function PortalApp({ assetBase = "" }: PortalAppProps) {
  const [view, setView] = useState<PortalView>("form-selector");

  if (view === "transfer-promotion") {
    return (
      <TransferPromotionForm
        assetBase={assetBase}
        onBack={() => setView("form-selector")}
      />
    );
  }

  return (
    <FormSelector
      assetBase={assetBase}
      onSelect={(id) => {
        if (id === "transfer-promotion") {
          setView("transfer-promotion");
        }
      }}
    />
  );
}
