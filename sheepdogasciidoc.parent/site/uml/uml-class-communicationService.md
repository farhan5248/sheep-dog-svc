# communicationService

## sendRequest function sends LSP requests to the server



Sends custom requests to the language server.

Handles async communication with the server.

**Examples**

```typescript
export function sendRequest(client: LanguageClient, method: string, params: any): Thenable<any>
// Calls: client.sendRequest(method, params)
```

## registerNotificationHandlers function sets up notification listeners



Registers handlers for custom notifications from the language server.

Allows the server to push updates to VS Code.

**Examples**

```typescript
export function registerNotificationHandlers(client: LanguageClient)
// Calls: client.onNotification for each custom notification type
```
