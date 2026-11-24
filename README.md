# 📦 Application Web JEE de Gestion de Produits

Application Web JEE complète développée avec **Spring Boot**, **Spring Data JPA**, **Hibernate**, **Thymeleaf**, et **Spring Security**.

> **Tutoriel vidéo de référence :** [Spring MVC, Thymeleaf, Spring Data JPA & Spring Security - Pr. Mohamed Youssfi](https://www.youtube.com/watch?v=FHy7raIldgg)

---

## 🎯 Objectifs du Projet

L'objectif de cette activité pratique est de concevoir et réaliser une application Web d'entreprise sécurisée permettant de gérer un catalogue de produits.

1. **Initialisation** du projet Spring Boot avec l'ensemble des dépendances requises.
2. **Conception du modèle** : Entité JPA `Product` avec annotations de validation Jakarta/Hibernate.
3. **Persistance des données** : Interface `ProductRepository` basée sur Spring Data JPA.
4. **Test de la couche DAO** : Initialisation et requêtes via un `CommandLineRunner`.
5. **Configuration Spring Security** : Personnalisation de la chaîne de filtres et gestion des accès.
6. **Interface utilisateur & Spring MVC** :
   - Affichage paginé / listé des produits.
   - Suppression sécurisée de produits (réservée à l'administrateur).
   - Templating avec Thymeleaf Layout Dialect et Bootstrap 5.
   - Formulaire d'ajout avec validation des champs (`@Valid`, `BindingResult`).
7. **Authentification & Autorisation (RBAC)** : Gestion des rôles `USER` et `ADMIN` en mémoire avec chiffrement BCrypt.
8. **Fonctionnalités avancées** : Recherche par mot-clé, gestion des accès non autorisés (`403 Forbidden`).

---

## 🛠️ Stack Technique

- **Langage :** Java 17 / 21
- **Framework Principal :** Spring Boot 3.x / 4.x
  - **Spring Web (Spring MVC)** : Gestion des requêtes HTTP et routage
  - **Spring Data JPA & Hibernate** : ORM et couche d'accès aux données
  - **Spring Security 6** : Authentification et contrôle d'accès basé sur les rôles
  - **Spring Validation (Hibernate Validator)** : Validation déclarative des formulaires
- **Moteur de Template :** Thymeleaf + `thymeleaf-layout-dialect` + `thymeleaf-extras-springsecurity6`
- **Frontend / UI :** Bootstrap 5.3 (via WebJars)
- **Bases de Données :**
  - **H2 Database** (en mémoire pour le développement & tests rapides)
  - **MySQL** (driver inclus pour passage en production)
- **Outils :** Lombok, Maven

---

## 📂 Structure du Projet

```text
src/main/
├── java/com/example/bdccmundiaspringmvc/
│   ├── entities/
│   │   └── Product.java               # Entité JPA avec validation Bean Validation
│   ├── repository/
│   │   └── ProductRepository.java     # Interface Spring Data JPA
│   ├── sec/
│   │   └── SecurityConfig.java        # Configuration de Spring Security (RBAC, Form Login)
│   ├── web/
│   │   └── ProductController.java     # Contrôleur Spring MVC
│   └── BdccMundiaSpringMvcApplication.java # Classe principale & CommandLineRunner (Seed DB)
└── resources/
    ├── application.properties         # Configuration de la base H2, port et console
    └── templates/
        ├── layout1.html               # Template maître (Navbar, Bootstrap, Layout Dialect)
        ├── products.html              # Vue liste des produits
        ├── new-product.html           # Formulaire de saisie d'un nouveau produit
        └── notAuthorized.html         # Page personnalisée Erreur 403 (Accès Refusé)
```

---

## 🚀 Étapes de Réalisation

### 1. Initialisation du Projet & Dépendances Maven

Configuration des dépendances dans le fichier [pom.xml](file:///pom.xml) :
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-validation`
- `thymeleaf-layout-dialect` & `thymeleaf-extras-springsecurity6`
- `webjars:bootstrap`
- `h2` & `mysql-connector-j`
- `lombok`

### 2. Entité JPA `Product`

L'entité `Product` intègre la validation des données avec Jakarta Validation et Lombok :

```java
@Entity
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString @Builder
public class Product {
    @Id @GeneratedValue
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 50)
    private String name;

    @Min(0)
    private double price;

    @Min(1)
    private double quantity;
}
```

### 3. Interface `ProductRepository`

Définition de l'interface étendant `JpaRepository` pour bénéficier sans code des opérations CRUD :

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Méthodes dérivées personnalisées de recherche (ex: findByNameContains)
    List<Product> findByNameContains(String keyword);
}
```

### 4. Test de la couche DAO

Dans la classe principale `BdccMundiaSpringMvcApplication`, utilisation d'un bean `CommandLineRunner` pour initialiser la base H2 au démarrage :

```java
@Bean
public CommandLineRunner start(ProductRepository productRepository) {
    return args -> {
        productRepository.save(Product.builder().name("iPhone 13").price(3200).quantity(100).build());
        productRepository.save(Product.builder().name("iPhone 15").price(3700).quantity(100).build());
        productRepository.save(Product.builder().name("iPhone 17").price(3900).quantity(100).build());

        productRepository.findAll().forEach(System.out::println);
    };
}
```

### 5 & 7. Sécurisation avec Spring Security

Configuration dans `SecurityConfig.java` :
- **Chiffrement des mots de passe :** `BCryptPasswordEncoder`.
- **Utilisateurs en mémoire (InMemoryUserDetailsManager) :**
  - Utilisateurs `User1` / `User2` avec le rôle `USER`
  - Utilisateur `admin` avec les rôles `USER` et `ADMIN`
- **Contrôle d'accès par URL (RBAC) :**
  - `/user/**` : Nécessite le rôle `USER`
  - `/admin/**` : Nécessite le rôle `ADMIN`
  - `/public/**` : Accès libre
  - Gestion de l'erreur d'autorisation avec redirection vers `/notAuthorized`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
            User.withUsername("User1").password(passwordEncoder().encode("1234")).roles("USER").build(),
            User.withUsername("admin").password(passwordEncoder().encode("1234")).roles("USER", "ADMIN").build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(ar -> ar
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(eh -> eh.accessDeniedPage("/notAuthorized"))
            .build();
    }
}
```

### 6. Contrôleur Spring MVC & Vues Thymeleaf

- **`ProductController.java`** :
  - `GET /user/index` : Affiche la liste des produits.
  - `GET /admin/newProduct` : Formulaire d'ajout.
  - `POST /admin/saveproduct` : Enregistrement avec `@Valid` et `BindingResult`.
  - `GET /admin/delete?id=...` : Suppression d'un produit.
  - `GET /notAuthorized` : Page d'erreur personnalisée.

- **Templating (`layout1.html`)** :
  - Intègre la barre de navigation Bootstrap avec profil utilisateur connecté (`sec:authentication="name"`).
  - Masquage conditionnel des boutons d'administration (`sec:authorize="hasRole('ADMIN')"`).
  - Affichage des messages d'erreurs de validation sous les champs du formulaire.

---

## 🔑 Identifiants de Connexion

| Nom d'utilisateur | Mot de passe | Rôles | Droits |
|---|---|---|---|
| **`User1`** | `1234` | `USER` | Consultation des produits |
| **`User2`** | `1234` | `USER` | Consultation des produits |
| **`admin`** | `1234` | `USER`, `ADMIN` | Consultation, Ajout et Suppression de produits |

---

## ⚙️ Installation et Exécution

### Prérequis
- Java JDK 17 ou supérieur
- Maven 3.8+ (ou utiliser le wrapper `mvnw` inclus)

### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd Activit-Pratique-N-3--Spring-MVC-Thymeleaf-main
```

### 2. Lancer l'application
```bash
./mvnw spring-boot:run
```
*(Sur Windows : `mvnw.cmd spring-boot:run`)*

### 3. Accès Web
- **Application :** [http://localhost:8084](http://localhost:8084) (redirection automatique vers `/user/index`)
- **Console H2 :** [http://localhost:8084/h2-console](http://localhost:8084/h2-console)
  - JDBC URL : `jdbc:h2:mem:products-db`
  - Utilisateur : `sa`
  - Mot de passe : *(vide)*

---

## 📸 Fonctionnalités Clés Implémentées

- [x] Architecture MVC standardisée JEE / Spring Boot
- [x] CRUD Produits avec persistance en base de données H2
- [x] Formulaire de saisie avec messages d'erreurs en cas de saisie invalide
- [x] Authentification par formulaire Spring Security
- [x] Contrôle d'accès basé sur les rôles (RBAC : USER vs ADMIN)
- [x] Boutons d'actions contextuels dans la vue selon le rôle de l'utilisateur
- [x] Page personnalisée en cas d'accès interdit (HTTP 403)
- [x] Template réutilisable avec Thymeleaf Layout Dialect & Bootstrap 5
