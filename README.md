# API Monitor
This project aims to develop a distributed API monitor to monitor the connectivity of third-party APIs.

It enables four deployment ways:
- **Standalone**: Deployed on a single machine to monitor APIs from public or internal network
- **Orchestrator-Runner using Message Queue**: Deployed on a multiple machine to monitor APIs, recommend for internal network (due to the safety concern of MQ).
  - Support MQ: Kafka
- **Orchestrator-Runner using Rest API**: Deployed on a multiple machine to monitor APIs for both public internal network.
- **CI/CD pipeline**: Deployed on GitHub Actions and use cron trigger to regularly run it for occasional monitoring.

## Core functionality (To Be Developed)
- [x] Monitor the connectivity of HTTP GET APIs.
- [ ] Support HTTP POST/PUT methods
- [ ] Validate the response of APIs with predefined expected result (compatible with OpenAPI standard)
- [ ] Visualise the result on a static website or in JSON/Markdown file.
- [ ] Support for chain of requests (for authentication chain or business chain)
- [ ] Support for RPC calls