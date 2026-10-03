# Global Staff Logger

Global Staff Logger is a high-performance logging plugin developed specifically for the Velocity Proxy. The primary goal is to intercept and log player activities across the entire network, allowing staff members to monitor players globally, regardless of which backend server they are currently connected to.

## Core Features

### 1. Command Logging
Log every command entered by a player across all linked servers, including commands that do not exist on the backend server.
- **Log Format:** `{player} ({server}) : {command}`
- **Example:** `Azady (Lobby) : /plugins`

### 2. Global Chat Logging
Log all chat messages sent by players in any server within the network.
- **Log Format:** `{player} ({server}) : {message}`
- **Example:** `Azady (Practice) : Hello`

### 3. Connection & Movement Logging
Log when a player switches between servers (via command, portal, or plugin) or when they are kicked/moved.
- **Log Format:** `{player} switched from {server} to {server}`
- **Example:** `Azady switched from Lobby to Practice`

### 4. Toggleable Spy Mode & Commands
Staff members can toggle Spy mode to monitor live activity in real-time across the network.
- `/spy chat` - Toggle global chat messages spy.
- `/spy cmd` - Toggle global command usage spy.
- `/spy sw` - Toggle server switches/movement spy.
- `/spy reload` or `/spyreload` - Reload `config.yml` and `messages.yml` configurations instantly.

### 5. Advanced Color & Formatting Support
Seamlessly supports both MiniMessage and Legacy color formatting together:
- **MiniMessage:** `<pink>`, `<red>`, `<gold>`, `<bold>`, `<gradient:#ff0000:#00ff00>`, `<rainbow>`, etc.
- **Legacy Codes:** `&a`, `&c`, `&d`, `&l`, `&pink`, etc.
- **Hex Colors:** `&#ffc0cb`, `&#FFAA00`, `<#ffc0cb>`, etc.

## Permissions

Each feature is controlled by a specific permission node:
- `nedayazady.spy.chat` - Access to `/spy chat` and receiving global chat logs.
- `nedayazady.spy.cmd` - Access to `/spy cmd` and receiving global command logs.
- `nedayazady.spy.sw` - Access to `/spy sw` and receiving connection/movement logs.
- `spy.nedayazady.reload` - Access to `/spyreload` and `/spy reload` to reload configurations.

## Building from source

1. Clone the repository.
2. Run `mvn clean package`.
3. The built jar will be located in the `target/` directory.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
