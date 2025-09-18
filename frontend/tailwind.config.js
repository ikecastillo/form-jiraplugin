/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{ts,tsx,js,jsx}",
    "./components/**/*.{ts,tsx,js,jsx}",
    "./lib/**/*.{ts,tsx,js,jsx}",
    "./plugin-entry.tsx",
  ],
  darkMode: ["class"],
  theme: {
    extend: {},
  },
  plugins: [require("tailwindcss-animate")],
};
