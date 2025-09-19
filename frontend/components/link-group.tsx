"use client";

import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ExternalLink } from "lucide-react";

export interface LinkItem {
  label: string;
  url: string;
  target?: "_self" | "_blank";
  description?: string;
}

export interface LinkGroupConfig {
  title?: string;
  links?: LinkItem[];
}

export interface LinkGroupProps {
  config?: LinkGroupConfig;
}

export default function LinkGroup({ config }: LinkGroupProps) {
  const title = config?.title ?? "Resources";
  const links = config?.links ?? [];

  return (
    <Card className="shadow-sm">
      <CardHeader>
        <CardTitle className="brand-heading text-xl">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        {links.length === 0 ? (
          <p className="text-sm text-muted-foreground">No links configured.</p>
        ) : (
          <ul className="space-y-3">
            {links.map((link) => (
              <li key={`${link.label}-${link.url}`}>
                <a
                  href={link.url}
                  target={link.target ?? "_blank"}
                  rel={link.target === "_blank" ? "noreferrer" : undefined}
                  className="group flex items-start gap-2 rounded-md border border-transparent px-3 py-2 transition hover:border-primary/30 hover:bg-primary/5"
                >
                  <ExternalLink className="mt-0.5 h-4 w-4 text-primary group-hover:scale-110 transition" />
                  <div>
                    <div className="font-medium text-foreground">{link.label}</div>
                    {link.description ? (
                      <p className="text-sm text-muted-foreground">{link.description}</p>
                    ) : null}
                  </div>
                </a>
              </li>
            ))}
          </ul>
        )}
      </CardContent>
    </Card>
  );
}
