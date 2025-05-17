from datetime import datetime
from flask import Flask, request, jsonify
from flask_cors import CORS
from sqlalchemy.orm import sessionmaker
from Connexion import engine
from OBJS import Reservacion, Usuario,Lugar,ReglaRequisito,LugarRegla, Base  # Si definiste la clase Usuario en models.py

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
@app.route('/lugares/<int:id>', methods=['GET'])
def obtener_lugar_por_id(id):
    session = Session()
    try:
        lugar = session.get(Lugar, id)  # ✅ Forma recomendada en SQLAlchemy 2.0

        if lugar:
            return jsonify({
                "success": True,
                "message": "Consulta exitosa",
                "lugar": lugar.to_dict()
            }), 200
        else:
            return jsonify({
                "success": False,
                "message": f"No se encontró ningún lugar con id {id}"
            }), 404
    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500
    finally:
        session.close()

@app.route('/usuario/<int:codigo>', methods=['PUT'])
def actualizar_usuario(codigo):
    session = Session()
    try:
        # Buscar el usuario por código
        usuario = session.query(Usuario).filter_by(codigo=codigo).first()

        if not usuario:
            return jsonify({"error": "Usuario no encontrado"}), 404

        # Obtener nuevos datos del formulario (solo se actualiza si se envía)
        password = request.form.get('password')
        nombre = request.form.get('nombre')
        apellidos = request.form.get('apellidos')
        telefono = request.form.get('telefono')
        correo = request.form.get('correo')
        imagen = request.files.get('imagen')

        if password:
            usuario.password = password
        if nombre:
            usuario.nombre = nombre
        if apellidos:
            usuario.apellidos = apellidos
        if telefono:
            usuario.telefono = telefono
        if correo:
            # Verificar si el correo ya lo usa otro usuario
            existe_correo = session.query(Usuario).filter(
                Usuario.correo == correo,
                Usuario.codigo != codigo
            ).first()
            if existe_correo:
                return jsonify({"error": "Este correo ya está en uso por otro usuario"}), 409
            usuario.correo = correo

        if imagen:
            usuario.imagen_perfil = imagen.read()

        session.commit()
        return jsonify({
            "success": True,
            "message": "Usuario actualizado correctamente",
            "user": usuario.to_dict()
        }), 200

    except Exception as e:
        session.rollback()
        print(e)
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

@app.route('/reglas', methods=['GET'])
def obtener_reglas():
    session = Session()
    try:
        reglas = session.query(ReglaRequisito).all()
        reglas_list = [regla.to_dict() for regla in reglas]

        return jsonify({
            "success": True,
            "message": "Consulta exitosa",
            "reglas": reglas_list
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()

@app.route('/regla', methods=['POST'])
def crear_regla():
    session = Session()
    try:
        nombre = request.form.get('nombre')
        descripcion = request.form.get('descripcion')
        enfuncion = request.form.get('enfuncion')
        responsable = request.form.get('responsable')

        if not all([nombre, descripcion, enfuncion]):
            return jsonify({"error": "Faltan campos obligatorios"}), 400

        nueva_regla = ReglaRequisito(
            nombre=nombre,
            descripcion=descripcion,
            enfuncion=enfuncion.lower() == 'true',
            responsable=responsable
        )

        session.add(nueva_regla)
        session.commit()

        return jsonify({
            "success": True,
            "message": "Regla registrada correctamente",
            "regla": nueva_regla.to_dict()
        }), 201

    except Exception as e:
        session.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        session.close() 
@app.route('/asociar-regla', methods=['POST'])
def asociar_regla_a_lugar():
    session = Session()
    try:
        id_lugar = request.form.get('id_lugar')
        id_regla = request.form.get('id_regla')

        if not id_lugar or not id_regla:
            return jsonify({"error": "Faltan parámetros"}), 400

        # Verificar si ya está asociada
        existe = session.query(LugarRegla).filter_by(id_lugar=id_lugar, id_regla=id_regla).first()
        if existe:
            return jsonify({"error": "Ya existe esta asociación"}), 409

        asociacion = LugarRegla(id_lugar=id_lugar, id_regla=id_regla)
        session.add(asociacion)
        session.commit()

        return jsonify({
            "success": True,
            "message": "Regla asociada al lugar correctamente"
        }), 201
    except Exception as e:
        session.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/asociaciones', methods=['GET'])
def obtener_asociaciones():
    session = Session()
    try:
        asociaciones = session.query(LugarRegla).all()
        data = [a.to_dict() for a in asociaciones]
        return jsonify({
            "success": True,
            "message": "Consulta exitosa",
            "asociaciones": data
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/lugar/<int:id_lugar>/reglas', methods=['GET'])
def obtener_reglas_de_lugar(id_lugar):
    session = Session()
    try:
        asociaciones = session.query(LugarRegla).filter_by(id_lugar=id_lugar).all()
        reglas = [a.regla.to_dict() for a in asociaciones if a.regla]
        return jsonify({
            "success": True,
            "message": f"Reglas asociadas al lugar {id_lugar}",
            "reglas": reglas
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/reservaciones', methods=['GET'])
def ver_reservaciones():
    session = Session()
    try:
        reservaciones = session.query(Reservacion).all()
        data = [r.to_dict() for r in reservaciones]
        return jsonify({
            "success": True,
            "message": "Reservaciones obtenidas correctamente",
            "reservaciones": data
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()
@app.route('/reservaciones/<int:id>', methods=['GET'])
def ver_reservacion_por_id(id):
    session = Session()
    try:
        reservacion = session.get(Reservacion, id)
        if reservacion:
            return jsonify({
                "success": True,
                "message": "Reservación encontrada",
                "reservacion": reservacion.to_dict()
            }), 200
        else:
            return jsonify({
                "success": False,
                "message": f"No se encontró reservación con id {id}"
            }), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()        

@app.route('/reservaciones', methods=['POST'])
def crear_reservacion():
    session = Session()
    try:
        idusuario = request.form.get('idusuario')
        idlugar = request.form.get('idlugar')
        fechareservacion = request.form.get('fechareservacion')  # formato esperado: 'YYYY-MM-DD'
        horareservacion = request.form.get('horareservacion')    # formato esperado: 'HH:MM:SS'

        # Validar campos
        if not all([idusuario, idlugar, fechareservacion, horareservacion]):
            print("hola")
            return jsonify({"error": "Faltan campos obligatorios"}), 400

        # Parsear fecha y hora
        try:
            fecha = datetime.strptime(fechareservacion, '%d/%m/%Y').date()
            hora = datetime.strptime(horareservacion, '%H:%M').time()
        except ValueError as e :
            print(e)
            return jsonify({"error": "Formato de fecha o hora inválido"}), 400

        # Crear nueva reservación
        nueva_reservacion = Reservacion(
            idusuario=int(idusuario),
            idlugar=int(idlugar),
            fechareservacion=fecha,
            horareservacion=hora
        )

        session.add(nueva_reservacion)
        session.commit()

        return jsonify({
            "success": True,
            "message": "Reservación creada correctamente",
            "reservacion": nueva_reservacion.to_dict()
        }), 201

    except Exception as e:
        session.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()      
@app.route('/reservaciones/<int:id>', methods=['DELETE'])
def eliminar_reservacion(id):
    session = Session()
    try:
        reservacion = session.get(Reservacion, id)
        if not reservacion:
            return jsonify({
                "success": False,
                "message": f"No se encontró reservación con id {id}"
            }), 404

        session.delete(reservacion)
        session.commit()

        return jsonify({
            "success": True,
            "message": "Reservación eliminada correctamente"
        }), 200

    except Exception as e:
        session.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()

if __name__ == '__main__':
     app.run(host="0.0.0.0", port=4000, debug=True)
