/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        treeDark: "#0f2b25",     // Deep dark green background
        treeNeon: "#00e696",     // Vibrant neon green ('tree' logo)
        treeMint: "#7dfab8",     // Lighter mint green for waves/accents
        treeWhite: "#f8fdfa",    // Off-white with a hint of green
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
