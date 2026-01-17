# Restaurante Inteligente – Android App

Aplicación Android desarrollada en **Kotlin** para la gestión digital de pedidos en un restaurante, orientada a mejorar la experiencia del cliente y optimizar los procesos internos del negocio.

El proyecto permite a los clientes realizar pedidos desde su dispositivo móvil y a los administradores gestionar el stock de productos en tiempo real.

---

## 📱 Funcionalidades principales

### Usuario cliente
- Registro e inicio de sesión mediante **Firebase Authentication**
- Visualización de productos organizados por categorías (bebidas, comidas y postres)
- Selección de productos y cantidades mediante diálogos interactivos
- Visualización del consumo total antes de confirmar el pedido
- Solicitud de pago desde la aplicación
- Envío de factura resumida por correo electrónico

### Usuario administrador
- Acceso a un panel específico para administración
- Visualización del stock actual de productos
- Actualización de cantidades disponibles en tiempo real

---

## 🛠️ Tecnologías utilizadas

**Mobile**
- Kotlin
- Android SDK
- ViewBinding
- Navigation Component

**Backend / Servicios**
- Firebase Authentication
- Firebase Realtime Database
- Google Services

**Otros**
- Glide / Picasso (carga de imágenes)
- JavaMail (envío de correos electrónicos)

---

## 📦 Estructura del proyecto

Proyecto desarrollado siguiendo la estructura estándar de **Android Studio**, con un módulo principal `app` que contiene la lógica de la aplicación, la interfaz de usuario y la comunicación con los servicios de Firebase.

Los paquetes principales incluyen:
- `adapter` → Adaptadores para la visualización de listas de productos
- `dialogs` → Diálogos para selección de productos y cantidades
- `model` → Modelos de datos utilizados en la aplicación
- Activities y Fragments para la navegación y flujo de la app

---

## ▶️ Ejecución del proyecto

1. Clonar el repositorio
2. Abrir el proyecto en **Android Studio**
3. Configurar un proyecto en **Firebase** (Auth y Realtime Database)
4. Conectar el archivo `google-services.json`
5. Ejecutar la aplicación en un emulador o dispositivo físico

---

## 👤 Autor

**Marcos Salas**  
Backend Developer | Java & Kotlin | APIs | SQL  

🔗 [MarcosSalas](https://www.linkedin.com/in/desarrolladormarcossalas/)  
