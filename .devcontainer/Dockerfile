# Dev Container for Java & GraphHopper
# This image installs OpenJDK, Maven, and other useful tools for Java development.
FROM mcr.microsoft.com/devcontainers/java:17

# Install Maven (if not already included)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Install git (required for many Java projects)
RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

# Set up a non-root user (VS Code devcontainers best practice)
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Set working directory
WORKDIR /workspace

# [Optional] Expose ports for GraphHopper web UI and admin interface
EXPOSE 8989
EXPOSE 8900

# [Optional] Set default command
CMD ["/bin/bash"]
