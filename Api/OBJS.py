from sqlalchemy.orm import declarative_base
from sqlalchemy import Column, Integer, String, LargeBinary,Enum
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
