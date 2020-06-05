module.exports = {
  presets: [
    [
      "@vue/app",
      {
        useBuiltIns: "entry",
      },
    ],
  ],
  plugins: [
    "@babel/plugin-proposal-optional-chaining",
    [
      "transform-imports",
      {
        vuetify: {
          transform: "vuetify/es5/components/${member}", // eslint-disable-line no-template-curly-in-string
          preventFullImport: false,
        },
      },
    ],
  ],
}
