import angular from '@angular-eslint/eslint-plugin';
import templateParser from '@angular-eslint/template-parser';
import templatePlugin from '@angular-eslint/eslint-plugin-template';
import typescriptEslint from '@typescript-eslint/eslint-plugin';
import typescriptParser from '@typescript-eslint/parser';

export default [
  {
    files: ["**/*.ts"],
    ignores: ["**/api/**/*"],
    languageOptions: {
      parser: typescriptParser,
      parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module',
        project: './tsconfig.json'
      }
    },
    plugins: {
      '@angular-eslint': angular,
      '@typescript-eslint': typescriptEslint
    },
    rules: {

      ...typescriptEslint.configs.recommended.rules,
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'app',
          style: 'kebab-case'
        }
      ],
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'app',
          style: 'camelCase'
        }
      ],
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'warn',
      '@angular-eslint/prefer-inject': 'off',
      '@angular-eslint/no-output-native': 'error',
      '@angular-eslint/no-output-on-prefix': 'error',
      '@angular-eslint/no-outputs-metadata-property': 'error',
      '@angular-eslint/no-queries-metadata-property': 'error',
      '@angular-eslint/no-forward-ref': 'error',
      '@angular-eslint/no-attribute-decorator': 'error',
      '@angular-eslint/no-duplicates-in-metadata-arrays': 'error',
      '@angular-eslint/no-empty-lifecycle-method': 'error',
      '@angular-eslint/no-async-lifecycle-method': 'error',
      '@angular-eslint/no-lifecycle-call': 'error',
      '@angular-eslint/no-pipe-impure': 'error',
      '@angular-eslint/component-class-suffix': 'error',
      '@angular-eslint/directive-class-suffix': 'error',
      '@angular-eslint/contextual-lifecycle': 'error',
      '@angular-eslint/no-uncalled-signals': 'error',
      '@angular-eslint/no-implicit-take-until-destroyed': 'error'
    }
  },
  {
    files: ["**/*.html"],
    ignores: ["**/api/**/*"],
    languageOptions: {
      parser: templateParser
    },
    plugins: {
      '@angular-eslint/template': templatePlugin
    },
    rules: {
      '@angular-eslint/template/no-negated-async': 'error',
      '@angular-eslint/template/no-any': 'error'
    }
  }
];