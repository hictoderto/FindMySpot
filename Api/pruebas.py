"""import requests

url = "http://localhost:4000/usuario"

# Datos del formulario
data = {
    'codigo': '2',
    'password': 'clave123',
    'nombre': 'Ana',
    'apellidos': 'Gómez',
    'telefono': '987654321',
    'correo': 'ana@example.com'
}

# Imagen (cambia la ruta por una real)
files = {
    'imagen_perfil': open('imagen.jpg', 'rb')
}

response = requests.post(url, data=data, files=files)

print(response.status_code)
print(response.json())"""

import requests

url = "http://localhost:4000/login"
datos = {
    "codigo": 2,
    "password": "clave123"
}

respuesta = requests.post(url, json=datos)
print(respuesta.json())

