import * as React from "react";
import { createRoot, type Root } from "react-dom/client";
import OnboardingForm from "./app/page";

type BootContext = {
  mountNodeId?: string;
  baseUrl?: string;
};

const DEFAULT_MOUNT_ID = "hr-portal-root";
const ROOT_STORAGE_KEY = "__hrPortalRoot";

declare global {
  interface Window {
    React?: typeof React;
    initializeHRPortalApp?: (context?: BootContext) => void;
    HR_PORTAL_BOOTSTRAP?: BootContext;
    __hrPortalRoot?: Root;
  }

  var initializeHRPortalApp: (context?: BootContext) => void;
}

if (typeof window !== "undefined" && !window.React) {
  window.React = React;
}

function getMountNode(mountNodeId?: string) {
  const resolvedId = mountNodeId || DEFAULT_MOUNT_ID;
  const node = document.getElementById(resolvedId);
  if (!node) {
    console.error("HR Portal bootstrap: mount node not found", resolvedId);
    return null;
  }
  return node;
}

function renderApp(target: Element) {
  const root = createRoot(target);
  root.render(
    <React.StrictMode>
      <OnboardingForm />
    </React.StrictMode>,
  );
  return root;
}

function bootstrap(context: BootContext = {}) {
  const node = getMountNode(context.mountNodeId);
  if (!node) {
    return;
  }

  window.HR_PORTAL_BOOTSTRAP = context;

  if (window[ROOT_STORAGE_KEY]) {
    try {
      window[ROOT_STORAGE_KEY]?.unmount();
    } catch (error) {
      console.warn("HR Portal bootstrap: failed to unmount existing root", error);
    }
  }

  window[ROOT_STORAGE_KEY] = renderApp(node);
}

globalThis.initializeHRPortalApp = bootstrap;
