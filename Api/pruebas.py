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

"""import requests

url = "http://localhost:4000/login"
datos = {
    "codigo": 2,
    "password": "clave123"
}

respuesta = requests.post(url, json=datos)
print(respuesta.json())"""
"""import requests

# URL de tu endpoint para crear un lugar
url = "http://localhost:4000/lugar"

# Datos del formulario (ajusta según el lugar que desees agregar)
data = {
    'nombre': 'gran hola',
    'capacidad': '20',
    'tipo_lugar': 'area verde'  # Debe ser uno de: 'area verde', 'bancos', 'salon', 'auditorio'
}

# Imagen del lugar (ajusta la ruta al archivo real en tu sistema)
files = {
    'imagen': open('im3.jpg', 'rb')  # Asegúrate de que el archivo exista
}

# Enviar la solicitud POST
response = requests.post(url, data=data, files=files)

# Imprimir resultado
print("Código de respuesta:", response.status_code)
print("Respuesta del servidor:", response.json())"""
"""import requests

url = 'http://localhost:4000/regla'  # Cambia por la URL correcta si es necesario

data = {
    'nombre': 'Regla7',
    'descripcion': 'Descripcion de la regla',
    'enfuncion': True,
    'responsable': 'alumno'
}

response = requests.post(url, data=data)

print(response.status_code)
print(response.json())"""
import requests

url = 'http://localhost:4000/asociar-regla'  # Cambia por la URL correcta si es necesario
for i in range (0,8):
    for j in range (0,8):   
        data = {
        'id_lugar': i,
        'id_regla': j,
        }
        response = requests.post(url, data=data)

        print(response.status_code)
        print(response.json())




