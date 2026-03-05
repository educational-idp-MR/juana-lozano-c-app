# Telemetry Lab

Stack de observabilidad con **Prometheus**, **Grafana** y **Loki**, acompañado de una aplicación Java (Spring Boot) que implementa un servicio de acortamiento de URLs.

Este laboratorio te permitirá observar métricas reales, logs y comportamientos de un sistema en ejecución.

## Quick Access:

Accede al laboratiorio completo [Aquí](https://educational-idp-mr.github.io/idp-monitoring-lab/)
Llena tu bitacora [Aquí](./Bitacora.md)

## Comprendiendo el dominio de la aplicación URL Shortener

La aplicación utilizada en este laboratorio implementa un servicio básico de **acortamiento de URLs**, un patrón ampliamente utilizado en plataformas web. Para comprender su comportamiento y poder analizar métricas y logs de forma adecuada, es importante entender los componentes fundamentales del dominio.

---

### Creación de URLs acortadas
Los usuarios pueden enviar una URL original y, opcionalmente, un código corto personalizado.  
Cuando esto ocurre, la aplicación:

- Valida que la URL sea válida.
- Genera un código corto aleatorio si no se proporciona uno.
- Asegura que el código corto no exista previamente.
- Almacena la relación `{shortCode → originalUrl}` junto con un timestamp.
---

### Redirección mediante shortCode
Cuando un cliente accede a `GET /api/{shortCode}`, la aplicación:

- Busca el código corto en el almacenamiento en memoria.
- Si existe redirige a la URL original.
- Si no existe responde con un estado de error.
---

### Almacenamiento temporal en memoria
La aplicación almacena toda la información en una estructura en memoria.  
Esto implica que:

- Los datos existen solo mientras la aplicación esté activa.
- El almacenamiento no persiste entre reinicios.
- El tamaño del almacenamiento depende exclusivamente de las solicitudes recibidas.
---

## Iniciar ambiente local


```bash
docker-compose up -d
```

## Acceder a los servicios

- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9091
- **Java App**: http://localhost

##  Endpoints de la aplicación URL Shortener

La aplicación expone una API REST simple con los siguientes endpoints:

### Lista de Endpoints

1. **`GET /api/`** – Información básica sobre el servicio.

2. **`POST /api/shorten`** – Crea una URL acortada.  
   Body esperado:
   ```json
   {
     "url": "https://ejemplo.com",
     "customCode": "opcional"
   }
   ```

3. **`GET /api/{shortCode}`** – Redirige a la URL original asociada.

4. **`GET /api/urls`** – Devuelve todas las URLs almacenadas.

---

## Cómo generar tráfico de forma rápida

### 🧪 Con `curl`

```bash
curl -X POST http://localhost/api/shorten   -H "Content-Type: application/json"   -d '{"url": "https://google.com"}'
```

```bash
curl -I http://localhost/api/abc123
```

```bash
curl http://localhost/api/urls
```

---

### Con navegador
- Abre `http://localhost/api/`
- Visita: `http://localhost/api/{codigo}`

---

## Comandos útiles

### Reiniciar servicios (mantiene dashboards y datos)
```bash
docker-compose restart
```

### Detener servicios (mantiene dashboards y datos)
```bash
docker-compose down
```

### Detener y ELIMINAR TODO (dashboards, métricas, logs)
```bash
docker-compose down -v
```

### Ver logs
```bash
docker-compose logs -f
docker-compose logs -f grafana
```

## Persistencia de Dashboards

Los dashboards que crees en Grafana se guardan automáticamente en el volumen Docker `grafana-storage`.

**Esto significa:**
- Tus dashboards sobreviven a `docker-compose down` y `docker-compose up`
- Se pierden solo con `docker-compose down -v` (elimina volúmenes)

## Estructura

```
telemetry-lab/
├── grafana-data/               # Configuración de Grafana
│   ├── datasources.yaml        # Prometheus y Loki
├── java-application/           # App Spring Boot con métricas
├── loki-data/                  # Config de loki para recoleccion de logs
└── docker-compose.yaml         # Orquestación de servicios
```

## 🎯 Workflow típico

```bash
# 1. Iniciar todo
docker-compose up -d

# 2. Trabajar en Grafana (crear dashboards, etc.)

# 3. Detener sin perder dashboards
docker-compose down

# 4. Reiniciar cuando quieras
docker-compose up -d

# 5. Reset completo (opcional)
docker-compose down -v
docker-compose up -d
```
