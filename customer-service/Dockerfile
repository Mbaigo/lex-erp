# 1. Image de base légère avec Java 17
FROM eclipse-temurin:21-jre-alpine

# 2. Informations sur le mainteneur (Optionnel mais recommandé)
LABEL maintainer="Lex Couture ERP"
LABEL version="1.0.0"

# 3. Création d'un répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# 4. Copie du fichier .jar compilé depuis ton ordinateur vers le conteneur
# Le nom cible généré par Maven se trouve dans le dossier target/
COPY target/*.jar app.jar

# 5. On indique le port sur lequel l'application écoute (Par défaut 8080 pour Spring Boot)
EXPOSE 8080

# 6. La commande qui sera exécutée au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]