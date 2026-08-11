package org.openrewrite.jpa;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class AddAllocationSizeToSequenceGeneratorTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AddAllocationSizeToSequenceGenerator())
          .parser(JavaParser.fromJavaVersion().classpath("hibernate-core", "javax.persistence-api"));
    }

    @Test
    void basic() {
        rewriteRun(
          //language=java
          java(
            """
              import javax.persistence.Entity;
              import javax.persistence.GeneratedValue;
              import javax.persistence.GeneratedValue;
              import javax.persistence.SequenceGenerator;
              import javax.persistence.Id;

              @Entity
              public class Customer {

                  @Id
                  @GeneratedValue(
                      strategy = GenerationType.SEQUENCE,
                      generator = "customer_seq"
                  )
                  @SequenceGenerator(name = "customer_seq", sequenceName = "customer_id_seq")
                  private Long id;

                  private String firstName;
                  private String lastName;

                  protected Customer() {}

                  public Customer(String firstName, String lastName) {
                      this.firstName = firstName;
                      this.lastName = lastName;
                  }

                  @Override
                  public String toString() {
                      return String.format(
                              "Customer[id=%d, firstName='%s', lastName='%s']",
                              id, firstName, lastName);
                  }

                  public Long getId() {
                      return id;
                  }

                  public String getFirstName() {
                      return firstName;
                  }

                  public String getLastName() {
                      return lastName;
                  }
              }
              """,
            """
              import javax.persistence.Entity;
              import javax.persistence.GeneratedValue;
              import javax.persistence.GeneratedValue;
              import javax.persistence.SequenceGenerator;
              import javax.persistence.Id;

              @Entity
              public class Customer {

                  @Id
                  @GeneratedValue(
                      strategy = GenerationType.SEQUENCE,
                      generator = "customer_seq"
                  )
                  @SequenceGenerator(name = "customer_seq", sequenceName = "customer_id_seq", allocationSize=1)
                  private Long id;

                  private String firstName;
                  private String lastName;

                  protected Customer() {}

                  public Customer(String firstName, String lastName) {
                      this.firstName = firstName;
                      this.lastName = lastName;
                  }

                  @Override
                  public String toString() {
                      return String.format(
                              "Customer[id=%d, firstName='%s', lastName='%s']",
                              id, firstName, lastName);
                  }

                  public Long getId() {
                      return id;
                  }

                  public String getFirstName() {
                      return firstName;
                  }

                  public String getLastName() {
                      return lastName;
                  }
              }
              """
          ));
    }

    @Test
    void noChange() {
        rewriteRun(
          //language=java
          java(
            """
              import javax.persistence.Entity;
              import javax.persistence.GeneratedValue;
              import javax.persistence.GenerationType;
              import javax.persistence.Id;

              @Entity
              public class Customer {

                  @Id
                  @GeneratedValue(strategy=GenerationType.AUTO)
                  private Long id;
                  private String firstName;
                  private String lastName;

                  protected Customer() {}

                  public Customer(String firstName, String lastName) {
                      this.firstName = firstName;
                      this.lastName = lastName;
                  }

                  @Override
                  public String toString() {
                      return String.format(
                              "Customer[id=%d, firstName='%s', lastName='%s']",
                              id, firstName, lastName);
                  }

                  public Long getId() {
                      return id;
                  }

                  public String getFirstName() {
                      return firstName;
                  }

                  public String getLastName() {
                      return lastName;
                  }
              }
              """
          ));
    }

}
