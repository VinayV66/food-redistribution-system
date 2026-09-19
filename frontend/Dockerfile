# ============================================================
# Frontend Dockerfile
# Multi-stage build:
#   Stage 1: Node.js build (creates the production React bundle)
#   Stage 2: Nginx serves the static files
# ============================================================

# Stage 1: Build React app
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files first (Docker cache optimization)
COPY package*.json ./
RUN npm ci --silent

# Copy all source files and build
COPY . .
RUN npm run build

# Stage 2: Nginx runtime
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx files
RUN rm -rf ./*

# Copy built React app from Stage 1
COPY --from=build /app/dist .

# Copy nginx config for React Router (handles client-side routes)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
