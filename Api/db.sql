create database findmyspotdb;
use findmyspotdb;
CREATE TABLE usuarios (
    codigo INT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100) UNIQUE,
    imagen_perfil LONGBLOB
);
CREATE TABLE lugares (
    id_lugar INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL,
    tipo_lugar ENUM('area verde', 'bancos', 'salon', 'auditorio') NOT NULL,
    imagen LONGBLOB
);

