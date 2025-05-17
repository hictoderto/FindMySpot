from sqlalchemy.orm import declarative_base,relationship
from sqlalchemy import Column, Integer, String, LargeBinary,Enum,Boolean,ForeignKey,Date,Time,UniqueConstraint
import base64
Base = declarative_base()

class Usuario(Base):
    __tablename__ = 'usuarios'

    codigo = Column(Integer, primary_key=True)
    password = Column(String(255), nullable=False)
    nombre = Column(String(100), nullable=False)
    apellidos = Column(String(150), nullable=False)
    telefono = Column(String(20))
    correo = Column(String(100), unique=True)
    imagen_perfil = Column(LargeBinary)

    def to_dict(self):
        imagen_base64 = base64.b64encode(self.imagen_perfil).decode('utf-8')
        return{
            "codigo": self.codigo,
            "nombre": self.nombre,
            "apellidos": self.apellidos,
            "correo": self.correo,
            "telefono": self.telefono,
            "imagen":imagen_base64
        }
    
class Lugar(Base):
    __tablename__ = 'lugares'

    id_lugar = Column(Integer, primary_key=True)
    nombre = Column(String(100), nullable=False)
    capacidad = Column(Integer, nullable=False)
    tipo_lugar = Column(Enum('area verde', 'bancos', 'salon', 'auditorio'), nullable=False)
    imagen = Column(LargeBinary)

    def to_dict(self):
        imagen_base64 = base64.b64encode(self.imagen).decode('utf-8') if self.imagen else None
        return {
            "id_lugar": self.id_lugar,
            "nombre": self.nombre,
            "capacidad": self.capacidad,
            "tipo_lugar": self.tipo_lugar,
            "imagen": imagen_base64
        }

class ReglaRequisito(Base):
    __tablename__ = 'reglas_requisitos'

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(100), nullable=False)
    descripcion = Column(String(255), nullable=False)
    enfuncion = Column(Boolean, nullable=False)
    responsable = Column(String(100))

    def to_dict(self):
        return {
            "id": self.id,
            "nombre": self.nombre,
            "descripcion": self.descripcion,
            "enfuncion": self.enfuncion,
            "responsable": self.responsable
        }
class LugarRegla(Base):
    __tablename__ = 'lugares_reglas'

    id_lugar = Column(Integer, ForeignKey('lugares.id_lugar'), primary_key=True)
    id_regla = Column(Integer, ForeignKey('reglas_requisitos.id'), primary_key=True)

    lugar = relationship("Lugar", backref="lugares_reglas")
    regla = relationship("ReglaRequisito", backref="lugares_reglas")

    def to_dict(self):
        return {
            "id_lugar": self.id_lugar,
            "id_regla": self.id_regla,
            "lugar": self.lugar.nombre if self.lugar else None,
            "regla": self.regla.nombre if self.regla else None
        }
class Reservacion(Base):
    __tablename__ = 'reservaciones'

    id = Column(Integer, primary_key=True, autoincrement=True)
    idusuario = Column(Integer, ForeignKey('usuarios.codigo'), nullable=False)
    idlugar = Column(Integer, ForeignKey('lugares.id_lugar'), nullable=False)
    fechareservacion = Column(Date, nullable=False)
    horareservacion = Column(Time, nullable=False)

    usuario = relationship("Usuario", backref="reservaciones")
    lugar = relationship("Lugar", backref="reservaciones")

    __table_args__ = (
        UniqueConstraint('idusuario', 'idlugar', 'fechareservacion', 'horareservacion', name='uq_reservacion'),
    )

    def to_dict(self):
        return {
            "id": self.id,
            "usuario": {
                "codigo": self.usuario.codigo,
                "nombre": self.usuario.nombre,
                "apellidos": self.usuario.apellidos
            } if self.usuario else None,
            "lugar": {
                "id_lugar": self.lugar.id_lugar,
                "nombre": self.lugar.nombre
            } if self.lugar else None,
            "fechareservacion": self.fechareservacion.isoformat(),
            "horareservacion": self.horareservacion.strftime("%H:%M:%S")
        }