import { configs } from 'angular-eslint';
import typescriptEslint from 'typescript-eslint';

const forFiles = (files, entries) =>
  entries.map((entry) => ({ ...entry, files }));

export default [
  {
    ignores: ['**/api/**/*'],
  },
  ...forFiles(
    ['**/*.ts'],
    [...configs.tsRecommended, ...typescriptEslint.configs.recommended],
  ),
  {
    files: ['**/*.ts'],
    rules: {
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'app',
          style: 'kebab-case',
        },
      ],
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'app',
          style: 'camelCase',
        },
      ],
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'warn',
      '@angular-eslint/prefer-inject': 'off',
    },
  },
  ...forFiles(
    ['**/*.html'],
    [
      ...configs.templateRecommended,
      ...configs.templateAccessibility,
    ],
  ),
  {
    files: ['**/*.html'],
    rules: {
      '@angular-eslint/template/prefer-control-flow': 'warn',
    },
  },
];
