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
    extend: {
      fontFamily: {
        'century-gothic': ['Century Gothic', 'sans-serif'],
      },
      colors: {
        'switch-red': '#d31820',
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
};
