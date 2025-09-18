"use client";

import { motion } from "framer-motion";
import { ArrowRight, UserPlus, RefreshCw, Shield, FileText } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { SwitchLogo } from "@/components/switch-logo";

type FormId =
  | "transfer-promotion"
  | "new-user"
  | "security-access"
  | "equipment-request";

interface FormOption {
  id: FormId;
  title: string;
  description: string;
  icon: typeof RefreshCw;
  available: boolean;
  color: string;
}

const formOptions: FormOption[] = [
  {
    id: "transfer-promotion",
    title: "Employee Transfer & Promotion Checklist",
    description:
      "Complete onboarding form for employee transfers and promotions. Ensure smooth transitions into new roles.",
    icon: RefreshCw,
    available: true,
    color: "border-primary/20 hover:border-primary/40",
  },
  {
    id: "new-user",
    title: "New User Onboarding Checklist",
    description:
      "Comprehensive onboarding process for new hires. Set up accounts, equipment, and access permissions.",
    icon: UserPlus,
    available: false,
    color: "border-border hover:border-muted-foreground/30",
  },
  {
    id: "security-access",
    title: "Security Access Request",
    description:
      "Request additional security clearances and facility access permissions for existing employees.",
    icon: Shield,
    available: false,
    color: "border-border hover:border-muted-foreground/30",
  },
  {
    id: "equipment-request",
    title: "Equipment & Resource Request",
    description:
      "Request IT equipment, office supplies, and other resources for employees and departments.",
    icon: FileText,
    available: false,
    color: "border-border hover:border-muted-foreground/30",
  },
];

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
    },
  },
};

const cardVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.4 },
  },
};

interface FormSelectorProps {
  assetBase?: string;
  onSelect: (id: FormId) => void;
}

export function FormSelector({ assetBase = "", onSelect }: FormSelectorProps) {
  const handleFormSelect = (option: FormOption) => {
    if (option.available) {
      onSelect(option.id);
    }
  };

  return (
    <div className="w-full max-w-6xl mx-auto py-8 px-4">
      <motion.div
        className="text-center mb-12"
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <div className="flex items-center justify-center mb-6">
          <SwitchLogo size="lg" assetBase={assetBase} />
        </div>
        <h1 className="brand-heading text-4xl text-foreground mb-4">HR Portal</h1>
        <p className="text-muted-foreground text-xl leading-relaxed max-w-2xl mx-auto">
          Select the appropriate form to begin your HR process. Complete forms ensure smooth operations and compliance.
        </p>
      </motion.div>

      <motion.div
        className="grid grid-cols-1 md:grid-cols-2 gap-6"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {formOptions.map((option) => {
          const IconComponent = option.icon;
          const cardClasses = [
            "group h-full cursor-pointer transition-all duration-300",
            option.color,
            option.available ? "hover:shadow-lg" : "opacity-60 cursor-not-allowed",
          ]
            .filter(Boolean)
            .join(" ");

          return (
            <motion.div
              key={option.id}
              variants={cardVariants}
              whileHover={{ scale: option.available ? 1.02 : 1 }}
              whileTap={{ scale: option.available ? 0.98 : 1 }}
            >
              <Card className={cardClasses} onClick={() => handleFormSelect(option)}>
                <CardHeader className="pb-4">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center space-x-3">
                      <div
                        className={`p-3 rounded-lg ${
                          option.available ? "bg-primary/10 text-primary" : "bg-muted text-muted-foreground"
                        }`}
                      >
                        <IconComponent className="w-6 h-6" />
                      </div>
                      <div className="flex-1">
                        <CardTitle className="brand-heading text-lg leading-tight">{option.title}</CardTitle>
                      </div>
                    </div>
                    {option.available && (
                      <ArrowRight className="w-5 h-5 text-primary opacity-0 group-hover:opacity-100 transition-opacity" />
                    )}
                  </div>
                </CardHeader>
                <CardContent>
                  <CardDescription className="text-base leading-relaxed">{option.description}</CardDescription>
                  {!option.available && (
                    <div className="mt-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground">
                        Coming Soon
                      </span>
                    </div>
                  )}
                </CardContent>
              </Card>
            </motion.div>
          );
        })}
      </motion.div>

      <motion.div
        className="text-center mt-12"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.6, delay: 0.4 }}
      >
        <p className="text-sm text-muted-foreground">
          Need help? Contact HR at <span className="text-switch-red">hr@switch.com</span> or extension 1234
        </p>
      </motion.div>
    </div>
  );
}
