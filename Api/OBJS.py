from sqlalchemy.orm import declarative_base
from sqlalchemy import Column, Integer, String, LargeBinary

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
