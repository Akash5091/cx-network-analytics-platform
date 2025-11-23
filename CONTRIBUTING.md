# Contributing to CX Network Analytics Platform

Thank you for your interest in contributing to this project!

## Getting Started

1. Fork the repository
2. Clone your fork locally
3. Create a feature branch (`git checkout -b feature/amazing-feature`)
4. Make your changes
5. Test your changes thoroughly
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to your branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose

### Running Locally

```bash
# Start all services
docker compose up --build

# Or run individual services for development
cd event-ingestion-service && mvn spring-boot:run
cd analytics-service && mvn spring-boot:run
cd api-service && mvn spring-boot:run
cd dashboard-web && npm run dev
```

## Code Style

### Java
- Follow standard Java conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and small

### JavaScript/React
- Use functional components with hooks
- Follow React best practices
- Use descriptive component and variable names
- Keep components small and reusable

## Testing

- Write unit tests for new features
- Ensure all tests pass before submitting PR
- Test end-to-end flows manually

## Pull Request Guidelines

- Provide a clear description of the changes
- Reference any related issues
- Include screenshots for UI changes
- Ensure your code builds without errors
- Update documentation if needed

## Code of Conduct

Be respectful and constructive in all interactions.

## Questions?

Feel free to open an issue for any questions or concerns.