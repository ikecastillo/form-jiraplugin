"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { ChevronLeft, ChevronRight, Check, Loader2, Shirt, ShieldCheck, HardHat, UtensilsCrossed, ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { toast } from "sonner";
import { cn } from "@/lib/utils";
import { SwitchLogo } from "@/components/switch-logo";

type TransferPromotionFormProps = {
  assetBase?: string;
  onBack?: () => void;
};


const steps = [
  { id: "employee", title: "Employee Info" },
  { id: "branding", title: "Branding & Security" },
  { id: "procurement", title: "Procurement" },
  { id: "it", title: "IT & Access" },
  { id: "accounting", title: "Accounting & Notes" },
  { id: "confirmation", title: "Confirmation" },
];

const departmentOptions = [
  { value: "operations", label: "Operations" },
  { value: "human-resources", label: "Human Resources" },
  { value: "finance", label: "Finance" },
  { value: "information-technology", label: "Information Technology" },
  { value: "facilities", label: "Facilities" },
];

const subDepartmentOptions = [
  { value: "not-applicable", label: "Not Applicable" },
  { value: "data-center", label: "Data Center" },
  { value: "network-operations", label: "Network Operations" },
  { value: "training", label: "Training" },
  { value: "other", label: "Other" },
];

const shiftOptions = [
  { value: "day", label: "Day" },
  { value: "swing", label: "Swing" },
  { value: "night", label: "Night" },
];

const locationOptions = [
  { value: "las-vegas", label: "LAS Campus" },
  { value: "atlanta", label: "ATL Campus" },
  { value: "reno", label: "RNO Campus" },
  { value: "remote", label: "Remote" },
];

const securityOrientationOptions = [
  { value: "scheduled", label: "Scheduled" },
  { value: "completed", label: "Completed" },
  { value: "not-required", label: "Not Required" },
];

const pantColorOptions = [
  { value: "black", label: "Black" },
  { value: "navy", label: "Navy" },
  { value: "charcoal", label: "Charcoal" },
  { value: "khaki", label: "Khaki" },
  { value: "custom", label: "Custom" },
];

const procurementOptions = [
  { value: "uniform", label: "Uniform", icon: Shirt },
  { value: "fr-uniform", label: "FR Uniform", icon: ShieldCheck },
  { value: "steel-toed-boots", label: "Steel-toed Boots", icon: HardHat },
  {
    value: "grubhub-day-shift",
    label: "GrubHub",
    icon: UtensilsCrossed,
  },
];

const itNeedOptions = [
  { value: "executive", label: "Executive" },
  { value: "desk-phone", label: "Desk Phone" },
  { value: "desktop-pc", label: "Desktop PC" },
  { value: "laptop", label: "Laptop" },
  { value: "vpn-access", label: "VPN Access" },
  { value: "voice-mail", label: "Voice Mail" },
  { value: "desk-build-out", label: "Desk Build-out" },
  { value: "laptop-dock", label: "Laptop Dock" },
];

const monitorOptions = [
  { value: "0", label: "0" },
  { value: "1", label: "1" },
  { value: "2", label: "2" },
  { value: "3", label: "3" },
];

const applicationRequestStatusOptions = [
  { value: "not-required", label: "Not Required" },
  { value: "submitted", label: "Submitted" },
  { value: "pending", label: "Pending" },
];

const cellPhoneOptions = [
  { value: "not-applicable", label: "N/A" },
  { value: "company-provided", label: "Company Provided" },
  { value: "reimbursement", label: "Reimbursement" },
];

const internetOptions = [
  { value: "not-applicable", label: "N/A" },
  { value: "reimbursement", label: "Internet Reimbursement" },
  { value: "company-plan", label: "Company Provided Plan" },
];

const formatOption = (
  options: { value: string; label: string }[],
  value: string,
) => {
  if (!value) {
    return "Not provided";
  }

  return options.find((option) => option.value === value)?.label ?? value;
};

interface FormData {
  employeeName: string;
  effectiveDate: string;
  department: string;
  subDepartment: string;
  jobTitle: string;
  shift: string;
  reportsTo: string;
  location: string;
  officeRoom: string;
  attachments: string;
  brandingNotes: string;
  securityOrientation: string;
  securityNotes: string;
  procurementItems: string[];
  pantColor: string;
  procurementNotes: string;
  itNeeds: string[];
  tabletDetails: string;
  monitorCount: string;
  makeUserLike: string;
  itNotes: string;
  applicationRequestStatus: string;
  accountingCellPhone: string;
  accountingInternet: string;
  creditCardNeeded: boolean;
  accountingNotes: string;
  additionalComments: string;
  confirmationAcknowledged: boolean;
}

const fadeInUp = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.3 } },
};

const contentVariants = {
  hidden: { opacity: 0, x: 50 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.3 } },
  exit: { opacity: 0, x: -50, transition: { duration: 0.2 } },
};

const TransferPromotionForm = ({ assetBase = "", onBack }: TransferPromotionFormProps) => {
  const [currentStep, setCurrentStep] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    employeeName: "",
    effectiveDate: "",
    department: "",
    subDepartment: "",
    jobTitle: "",
    shift: "",
    reportsTo: "",
    location: "",
    officeRoom: "",
    attachments: "",
    brandingNotes: "",
    securityOrientation: "",
    securityNotes: "",
    procurementItems: [],
    pantColor: "",
    procurementNotes: "",
    itNeeds: [],
    tabletDetails: "",
    monitorCount: "",
    makeUserLike: "",
    itNotes: "",
    applicationRequestStatus: "",
    accountingCellPhone: "",
    accountingInternet: "",
    creditCardNeeded: false,
    accountingNotes: "",
    additionalComments: "",
    confirmationAcknowledged: false,
  });

  const updateFormData = (
    field: keyof FormData,
    value: string | boolean,
  ) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const toggleProcurementItem = (item: string) => {
    setFormData((prev) => {
      const alreadySelected = prev.procurementItems.includes(item);
      return {
        ...prev,
        procurementItems: alreadySelected
          ? prev.procurementItems.filter((value) => value !== item)
          : [...prev.procurementItems, item],
      };
    });
  };

  const toggleItNeed = (need: string) => {
    setFormData((prev) => {
      const alreadySelected = prev.itNeeds.includes(need);
      return {
        ...prev,
        itNeeds: alreadySelected
          ? prev.itNeeds.filter((value) => value !== need)
          : [...prev.itNeeds, need],
      };
    });
  };

  const nextStep = () => {
    if (currentStep < steps.length - 1) {
      setCurrentStep((prev) => prev + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 0) {
      setCurrentStep((prev) => prev - 1);
    }
  };

  const handleSubmit = () => {
    setIsSubmitting(true);

    setTimeout(() => {
      toast.success("Form submitted successfully!");
      setIsSubmitting(false);
      setIsSubmitted(true);
    }, 1500);
  };

  const handleViewSubmission = () => {
    alert("In production this will go to jira ticket for this form");
  };

  const handleBackToHome = () => {
    if (typeof onBack === "function") {
      onBack();
      return;
    }

    setIsSubmitted(false);
    setCurrentStep(0);
  };

  const isStepValid = () => {
    switch (currentStep) {
      case 0:
        return (
          formData.employeeName.trim() !== "" &&
          formData.effectiveDate.trim() !== "" &&
          formData.department.trim() !== "" &&
          formData.jobTitle.trim() !== ""
        );
      case 1:
        return formData.securityOrientation.trim() !== "";
      case 2:
        return formData.pantColor.trim() !== "";
      case 3:
        return formData.monitorCount.trim() !== "";
      case 4:
        return (
          formData.accountingCellPhone.trim() !== "" &&
          formData.accountingInternet.trim() !== ""
        );
      case 5:
        return formData.confirmationAcknowledged;
      default:
        return true;
    }
  };

  // Show confirmation screen after successful submission
  if (isSubmitted) {
    return (
      <div className="w-full max-w-2xl mx-auto py-8">
        <motion.div
          className="text-center"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <div className="flex items-center justify-center mb-6">
            <SwitchLogo size="md" className="mr-4" assetBase={assetBase} />
          </div>
          <motion.div
            className="mb-8"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Check className="w-10 h-10 text-green-600" />
            </div>
            <h1 className="brand-heading text-3xl text-foreground mb-3">
              Submission Successful!
            </h1>
            <p className="text-muted-foreground text-lg leading-relaxed max-w-xl mx-auto">
              Your Employee Transfer & Promotion Checklist has been submitted successfully.
              The relevant departments will be notified and will begin processing your request.
            </p>
          </motion.div>

          <motion.div
            className="space-y-4"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
          >
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  onClick={handleBackToHome}
                  className="flex items-center gap-2 px-6 py-3 rounded-2xl"
                  size="lg"
                >
                  <ArrowLeft className="w-4 h-4" />
                  Back to HR Portal
                </Button>
              </motion.div>
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  onClick={handleViewSubmission}
                  variant="outline"
                  className="flex items-center gap-2 px-6 py-3 rounded-2xl"
                  size="lg"
                >
                  View Submission
                </Button>
              </motion.div>
            </div>
          </motion.div>

          <motion.div
            className="mt-8 p-4 bg-muted/50 rounded-xl"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5, delay: 0.6 }}
          >
            <p className="text-sm text-muted-foreground">
              <strong>Submission ID:</strong> TPC-{Date.now().toString().slice(-6)}
            </p>
            <p className="text-sm text-muted-foreground mt-1">
              <strong>Employee:</strong> {formData.employeeName}
            </p>
            <p className="text-sm text-muted-foreground mt-1">
              <strong>Effective Date:</strong> {formData.effectiveDate}
            </p>
          </motion.div>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="w-full max-w-2xl mx-auto py-8">
      {/* Back Button */}
      <motion.div
        className="mb-6"
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.4 }}
      >
        <Button
          variant="ghost"
          onClick={handleBackToHome}
          className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to HR Portal
        </Button>
      </motion.div>

      {/* Header */}
      <motion.div
        className="text-center mb-8"
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <div className="flex items-center justify-center mb-4">
          <SwitchLogo size="md" className="mr-4" assetBase={assetBase} />
        </div>
        <h1 className="brand-heading text-3xl text-foreground mb-3">
          Employee Transfer & Promotion Checklist
        </h1>
        <p className="text-muted-foreground text-lg leading-relaxed max-w-xl mx-auto">
          Complete this comprehensive onboarding form <strong className="text-switch-red">2 weeks prior</strong> to your employee's transfer or promotion date to ensure a smooth transition into their new role.
        </p>
      </motion.div>

      {/* Progress indicator */}
      <motion.div
        className="mb-8"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="flex justify-between mb-2">
          {steps.map((step, index) => (
            <motion.div
              key={index}
              className="flex flex-col items-center"
              whileHover={{ scale: 1.1 }}
            >
              <motion.div
                className={cn(
                  "w-4 h-4 rounded-full cursor-pointer transition-colors duration-300",
                  index < currentStep
                    ? "bg-primary"
                    : index === currentStep
                      ? "bg-primary ring-4 ring-primary/20"
                      : "bg-muted",
                )}
                onClick={() => {
                  if (index <= currentStep) {
                    setCurrentStep(index);
                  }
                }}
                whileTap={{ scale: 0.95 }}
              />
              <motion.span
                className={cn(
                  "text-xs mt-1.5 hidden sm:block",
                  index === currentStep
                    ? "text-primary font-medium"
                    : "text-muted-foreground",
                )}
              >
                {step.title}
              </motion.span>
            </motion.div>
          ))}
        </div>
        <div className="w-full bg-muted h-1.5 rounded-full overflow-hidden mt-2">
          <motion.div
            className="h-full bg-primary"
            initial={{ width: 0 }}
            animate={{ width: `${(currentStep / (steps.length - 1)) * 100}%` }}
            transition={{ duration: 0.3 }}
          />
        </div>
      </motion.div>

      {/* Form card */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
      >
        <Card className="border shadow-md rounded-3xl overflow-hidden">
          <div>
            <AnimatePresence mode="wait">
              <motion.div
                key={currentStep}
                initial="hidden"
                animate="visible"
                exit="exit"
                variants={contentVariants}
              >
                {currentStep === 0 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">Employee Details</CardTitle>
                      <CardDescription>
                        Provide the core information for this transfer or
                        promotion.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="employeeName">Employee Name</Label>
                          <Input
                            id="employeeName"
                            placeholder="Employee full name"
                            value={formData.employeeName}
                            onChange={(e) =>
                              updateFormData("employeeName", e.target.value)
                            }
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="effectiveDate">Effective Date</Label>
                          <Input
                            id="effectiveDate"
                            type="date"
                            value={formData.effectiveDate}
                            onChange={(e) =>
                              updateFormData("effectiveDate", e.target.value)
                            }
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          />
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="department">Department</Label>
                          <Select
                            value={formData.department}
                            onValueChange={(value) =>
                              updateFormData("department", value)
                            }
                          >
                            <SelectTrigger
                              id="department"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select department" />
                            </SelectTrigger>
                            <SelectContent>
                              {departmentOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="subDepartment">Sub-department</Label>
                          <Select
                            value={formData.subDepartment}
                            onValueChange={(value) =>
                              updateFormData("subDepartment", value)
                            }
                          >
                            <SelectTrigger
                              id="subDepartment"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select sub-department" />
                            </SelectTrigger>
                            <SelectContent>
                              {subDepartmentOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="jobTitle">Job Title</Label>
                        <Input
                          id="jobTitle"
                          placeholder="New job title"
                          value={formData.jobTitle}
                          onChange={(e) =>
                            updateFormData("jobTitle", e.target.value)
                          }
                          className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="reportsTo">Reports To</Label>
                        <Input
                          id="reportsTo"
                          placeholder="Manager or supervisor"
                          value={formData.reportsTo}
                          onChange={(e) =>
                            updateFormData("reportsTo", e.target.value)
                          }
                          className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {currentStep === 1 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">Branding & Security</CardTitle>
                      <CardDescription>
                        Configure branding materials and security requirements.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="shift">Shift</Label>
                          <Select
                            value={formData.shift}
                            onValueChange={(value) => updateFormData("shift", value)}
                          >
                            <SelectTrigger
                              id="shift"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select shift" />
                            </SelectTrigger>
                            <SelectContent>
                              {shiftOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="location">Location</Label>
                          <Select
                            value={formData.location}
                            onValueChange={(value) => updateFormData("location", value)}
                          >
                            <SelectTrigger
                              id="location"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select location" />
                            </SelectTrigger>
                            <SelectContent>
                              {locationOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="officeRoom">Office/Room</Label>
                          <Input
                            id="officeRoom"
                            placeholder="Room number"
                            value={formData.officeRoom}
                            onChange={(e) => updateFormData("officeRoom", e.target.value)}
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          />
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="securityOrientation">Security Orientation</Label>
                        <Select
                          value={formData.securityOrientation}
                          onValueChange={(value) => updateFormData("securityOrientation", value)}
                        >
                          <SelectTrigger
                            id="securityOrientation"
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          >
                            <SelectValue placeholder="Select status" />
                          </SelectTrigger>
                          <SelectContent>
                            {securityOrientationOptions.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="brandingNotes">Branding Notes</Label>
                        <Textarea
                          id="brandingNotes"
                          placeholder="Any specific branding requirements..."
                          value={formData.brandingNotes}
                          onChange={(e) => updateFormData("brandingNotes", e.target.value)}
                          className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary min-h-[100px]"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {currentStep === 2 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">Procurement</CardTitle>
                      <CardDescription>
                        Select required uniform items and equipment.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-6">
                      <motion.div variants={fadeInUp} className="space-y-4">
                        <Label className="text-base font-medium">Uniform Items</Label>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                          {procurementOptions.map((option) => {
                            const IconComponent = option.icon;
                            const isSelected = formData.procurementItems.includes(option.value);
                            return (
                              <motion.div
                                key={option.value}
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                className={cn(
                                  "relative p-4 border-2 rounded-xl cursor-pointer transition-all duration-300 flex flex-col items-center justify-center gap-2 aspect-square",
                                  isSelected
                                    ? "border-gray-800 bg-gray-100 shadow-md"
                                    : "border-border hover:border-gray-400 hover:bg-gray-50",
                                )}
                                onClick={() => toggleProcurementItem(option.value)}
                              >
                                <Checkbox
                                  checked={isSelected}
                                  onChange={() => toggleProcurementItem(option.value)}
                                  className="absolute top-2 right-2 form-component"
                                />
                                <IconComponent className="w-8 h-8 text-foreground/70" />
                                <span className="text-sm font-medium text-center leading-tight">
                                  {option.label}
                                </span>
                              </motion.div>
                            );
                          })}
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="pantColor">Pant Color</Label>
                        <Select
                          value={formData.pantColor}
                          onValueChange={(value) => updateFormData("pantColor", value)}
                        >
                          <SelectTrigger
                            id="pantColor"
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          >
                            <SelectValue placeholder="Select pant color" />
                          </SelectTrigger>
                          <SelectContent>
                            {pantColorOptions.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {currentStep === 3 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">IT & Access</CardTitle>
                      <CardDescription>
                        Configure IT equipment and system access requirements.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="space-y-4">
                        <Label className="text-base font-medium">IT Needs</Label>
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                          {itNeedOptions.map((option) => {
                            const isSelected = formData.itNeeds.includes(option.value);
                            return (
                              <motion.div
                                key={option.value}
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                className={cn(
                                  "flex items-center space-x-2 p-3 border rounded-lg cursor-pointer transition-all duration-300",
                                  isSelected
                                    ? "border-gray-800 bg-gray-100"
                                    : "border-border hover:border-gray-400",
                                )}
                                onClick={() => toggleItNeed(option.value)}
                              >
                                <Checkbox
                                  checked={isSelected}
                                  onChange={() => toggleItNeed(option.value)}
                                  className="form-component"
                                />
                                <span className="text-sm font-medium">{option.label}</span>
                              </motion.div>
                            );
                          })}
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="monitorCount">Monitor Count</Label>
                        <Select
                          value={formData.monitorCount}
                          onValueChange={(value) => updateFormData("monitorCount", value)}
                        >
                          <SelectTrigger
                            id="monitorCount"
                            className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                          >
                            <SelectValue placeholder="Select monitor count" />
                          </SelectTrigger>
                          <SelectContent>
                            {monitorOptions.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="makeUserLike">Make User Like</Label>
                        <Input
                          id="makeUserLike"
                          placeholder="Copy settings from existing user"
                          value={formData.makeUserLike}
                          onChange={(e) => updateFormData("makeUserLike", e.target.value)}
                          className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {currentStep === 4 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">Accounting & Notes</CardTitle>
                      <CardDescription>
                        Configure accounting details and additional requirements.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <motion.div variants={fadeInUp} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="accountingCellPhone">Cell Phone</Label>
                          <Select
                            value={formData.accountingCellPhone}
                            onValueChange={(value) => updateFormData("accountingCellPhone", value)}
                          >
                            <SelectTrigger
                              id="accountingCellPhone"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select cell phone option" />
                            </SelectTrigger>
                            <SelectContent>
                              {cellPhoneOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="accountingInternet">Internet</Label>
                          <Select
                            value={formData.accountingInternet}
                            onValueChange={(value) => updateFormData("accountingInternet", value)}
                          >
                            <SelectTrigger
                              id="accountingInternet"
                              className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary"
                            >
                              <SelectValue placeholder="Select internet option" />
                            </SelectTrigger>
                            <SelectContent>
                              {internetOptions.map((option) => (
                                <SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="flex items-center space-x-2">
                        <Checkbox
                          id="creditCardNeeded"
                          checked={formData.creditCardNeeded}
                          onCheckedChange={(checked) => updateFormData("creditCardNeeded", checked as boolean)}
                          className="form-component"
                        />
                        <Label htmlFor="creditCardNeeded" className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                          Credit Card Needed
                        </Label>
                      </motion.div>
                      <motion.div variants={fadeInUp} className="space-y-2">
                        <Label htmlFor="additionalComments">Additional Comments</Label>
                        <Textarea
                          id="additionalComments"
                          placeholder="Any additional notes or requirements..."
                          value={formData.additionalComments}
                          onChange={(e) => updateFormData("additionalComments", e.target.value)}
                          className="form-component transition-all duration-300 focus:ring-2 focus:ring-primary/20 focus:border-primary min-h-[120px]"
                        />
                      </motion.div>
                    </CardContent>
                  </>
                )}

                {currentStep === 5 && (
                  <>
                    <CardHeader>
                      <CardTitle className="brand-heading">Confirmation</CardTitle>
                      <CardDescription>
                        Review your submission and confirm all details are correct.
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-6">
                      <motion.div variants={fadeInUp} className="bg-muted/50 p-6 rounded-xl space-y-4">
                        <h3 className="font-semibold text-lg">Employee Information</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                          <div><strong>Name:</strong> {formData.employeeName || "Not provided"}</div>
                          <div><strong>Effective Date:</strong> {formData.effectiveDate || "Not provided"}</div>
                          <div><strong>Department:</strong> {formatOption(departmentOptions, formData.department)}</div>
                          <div><strong>Job Title:</strong> {formData.jobTitle || "Not provided"}</div>
                          <div><strong>Location:</strong> {formatOption(locationOptions, formData.location)}</div>
                          <div><strong>Shift:</strong> {formatOption(shiftOptions, formData.shift)}</div>
                        </div>
                      </motion.div>

                      <motion.div variants={fadeInUp} className="flex items-center space-x-2">
                        <Checkbox
                          id="confirmationAcknowledged"
                          checked={formData.confirmationAcknowledged}
                          onCheckedChange={(checked) => updateFormData("confirmationAcknowledged", checked as boolean)}
                          className="form-component"
                        />
                        <Label htmlFor="confirmationAcknowledged" className="text-sm leading-relaxed">
                          I acknowledge that all information provided is accurate and complete. I understand that this form will be processed by the appropriate departments.
                        </Label>
                      </motion.div>
                    </CardContent>
                  </>
                )}
              </motion.div>
            </AnimatePresence>

            <CardFooter className="flex justify-between pt-6 pb-4">
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  type="button"
                  variant="outline"
                  onClick={prevStep}
                  disabled={currentStep === 0}
                  className="flex items-center gap-1 transition-all duration-300 rounded-2xl"
                >
                  <ChevronLeft className="h-4 w-4" /> Back
                </Button>
              </motion.div>
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <Button
                  type="button"
                  onClick={
                    currentStep === steps.length - 1 ? handleSubmit : nextStep
                  }
                  disabled={!isStepValid() || isSubmitting}
                  className={cn(
                    "flex items-center gap-1 transition-all duration-300 rounded-2xl",
                    currentStep === steps.length - 1 ? "" : "",
                  )}
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="h-4 w-4 animate-spin" /> Submitting...
                    </>
                  ) : (
                    <>
                      {currentStep === steps.length - 1 ? "Submit" : "Next"}
                      {currentStep === steps.length - 1 ? (
                        <Check className="h-4 w-4" />
                      ) : (
                        <ChevronRight className="h-4 w-4" />
                      )}
                    </>
                  )}
                </Button>
              </motion.div>
            </CardFooter>
          </div>
        </Card>
      </motion.div>

      {/* Step indicator */}
      <motion.div
        className="mt-4 text-center text-sm text-muted-foreground"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.4 }}
      >
        Step {currentStep + 1} of {steps.length}
      </motion.div>
    </div>
  );
};

export default TransferPromotionForm;
