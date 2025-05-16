from flask import Flask, request, jsonify
from flask_cors import CORS
from sqlalchemy.orm import sessionmaker
from Connexion import engine
from OBJS import Usuario,Lugar, Base  # Si definiste la clase Usuario en models.py

app = Flask(__name__)
CORS(app)

Session = sessionmaker(bind=engine)
Base.metadata.create_all(engine)  # Crea las tablas si no existen

@app.route('/')
def ping():
    return jsonify({"message": "Bienvenido a findmy spot api!"})


@app.route('/usuario', methods=['POST'])
def crear_usuario():
    session = Session()

    try:
        # Datos de formulario
        codigo = request.form.get('codigo')
        password = request.form.get('password')
        nombre = request.form.get('nombre')
        apellidos = request.form.get('apellidos')
        telefono = request.form.get('telefono')
        correo = request.form.get('correo')
        

        if not all([codigo, password, nombre, apellidos, correo]):
            return jsonify({"error": "Faltan campos obligatorios"}), 400

        # Verifica si ya existe
        existe = session.query(Usuario).filter(
            (Usuario.codigo == codigo) | (Usuario.correo == correo)
        ).first()

        if existe:
            return jsonify({"error": "El usuario ya existe"}), 409

        # Imagen (opcional)
        imagen = request.files.get('imagen')
        imagen_bin = imagen.read() if imagen else None
        nuevo_usuario = Usuario(
            codigo=int(codigo),
            password=password,
            nombre=nombre,
            apellidos=apellidos,
            telefono=telefono,
            correo=correo,
            imagen_perfil=imagen_bin
        )

        session.add(nuevo_usuario)
        session.commit()

        return jsonify({
                        "success":True,
                        "message": "Usuario creado con imagen correctamente",
                        "user":nuevo_usuario.to_dict()}), 201

    except Exception as e:
        session.rollback()
        print(e)
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/login', methods=['POST'])
def login():
    session = Session()
    try:
        codigo = request.form.get('codigo')
        password = request.form.get('password')

        if not codigo or not password:
            return jsonify({"error": "Código y contraseña requeridos"}), 400

        # Busca al usuario por código
        usuario = session.query(Usuario).filter_by(codigo=codigo).first()

        if not usuario or usuario.password != password:
            return jsonify({"error": "Credenciales inválidas"}), 401

        # Opcionalmente puedes omitir la imagen del perfil para no sobrecargar
        return jsonify({
            "success":True,
            "message": "Login exitoso",
            "user": usuario.to_dict()
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

    finally:
        session.close()
@app.route('/lugares', methods=['GET'])
def obtener_lugares():
    session = Session()
    try:
        lugares = session.query(Lugar).all()
        lugar_list = [lugar.to_dict() for lugar in lugares]


        return jsonify({
            "success":True,
            "message": "consulta exitosa",
            "lugares": lugar_list
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/lugar', methods=['POST'])
def crear_lugar():
    session = Session()
    try:
        nombre = request.form.get('nombre')
        capacidad = request.form.get('capacidad')
        tipo_lugar = request.form.get('tipo_lugar')
        imagen_file = request.files.get('imagen')

        if not all([nombre, capacidad, tipo_lugar]):
            return jsonify({"error": "Faltan campos obligatorios"}), 400

        imagen_bin = imagen_file.read() if imagen_file else None

        nuevo_lugar = Lugar(
            nombre=nombre,
            capacidad=int(capacidad),
            tipo_lugar=tipo_lugar,
            imagen=imagen_bin
        )

        session.add(nuevo_lugar)
        session.commit()

        return jsonify({
            "success": True,
            "message": "Lugar creado correctamente",
            "lugar": nuevo_lugar.to_dict()
        }), 201

    except Exception as e:
        session.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()


if __name__ == '__main__':
     app.run(host="0.0.0.0", port=4000, debug=True)