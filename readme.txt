http://localhost:9000/hello-world

Also man kann sich eigentlich recht einfach die Konfiguration überladen inden man das einfach
via -D oder mit -- angibt. Dann wird dieses Property File zuerst geladen (first wins).

  cd target
  java -jar spring-boot-demo-0.1.0.jar --spring.config.location=file:../application-xxx.properties --debug

Ist auch beschrieben in

  https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

Man kann auch mehr als ein File angeben wenn das nötig wäre!

