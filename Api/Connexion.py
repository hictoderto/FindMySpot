from sqlalchemy import create_engine

usuario = "root"

contrasenia = "12345678"

puerto = "3306"

baseDatos="findmyspotdb"



engine = create_engine("mysql+pymysql://"+usuario+":"+contrasenia+"@localhost:"+puerto+"/"+baseDatos)


