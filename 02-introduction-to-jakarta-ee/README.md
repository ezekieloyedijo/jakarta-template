# Topic 02: Introduction to Jakarta EE

## The problem that existed

In topic 01 you built `MiniWebApp.java` by hand. Remember all the **plumbing**: opening a port,
reading raw text, decoding `%20`, matching paths, writing headers...

Now picture the late 1990s. **Every company** building web apps in Java was writing that same plumbing,
**each in its own way**:

- Company A's code only worked on **Company A's** server product. Want to switch? **Rewrite everything.**
- A developer who moved jobs had to **learn a new home-made system** at every company.
- Every team wasted months on plumbing and bugs (security holes, crashes with many users) instead of building **features**.

The industry needed **one shared set of rules** that says: *"This is how a Java web component
receives a request and sends a response. Any server that follows these rules can run any app that
follows these rules."* It also needed ready-made servers that **do the plumbing for you**.

Those rules were first called **J2EE**, then **Java EE**. Today they are called **Jakarta EE**.

## The end goal of this topic

You should be able to explain:

1. That Jakarta EE is a set of **specifications** (rules), not one program.
2. What a **container** is and what jobs it does for you, so that you only write the "recipe".
3. Why you see `jakarta.servlet...` in new code and `javax.servlet...` in old tutorials.
4. How our tools (Maven + Tomcat) fit in, and you should be able to **run your first Jakarta app**.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: the plumbing problem** | We start from the pain you felt in topic 01 |
| 2 | **Specification vs Implementation** | The cure for the pain: agree on *rules*, then many products can follow them |
| 3 | **Jakarta EE is a family of specifications** | Now that you know what a "spec" is: Jakarta EE is a *collection* of them, and we'll meet the ones in this course |
| 4 | **The Container** | The most important idea: the program that follows the specs and does the plumbing for you |
| 5 | **"Don't call us, we'll call you"** | How your code and the container work together (who calls whom) |
| 6 | **Kinds of servers** | With "container" understood, you can tell Tomcat/Jetty apart from GlassFish/WildFly |
| 7 | **The name story: Java EE to Jakarta EE, `javax` to `jakarta`** | Explains old tutorials, version numbers, and why Tomcat 9 won't run our code |
| 8 | **API vs Implementation in your project (`provided`)** | How a project "uses the rules" without containing the server |
| 9 | **Your first Jakarta web app** | All of the above in action, and a comparison with topic 01 |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | The smallest Jakarta app: one servlet (`/hello`) plus one page that **reports what the container does for you** (`/container-info`) | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **Topic 01's MiniWebApp rebuilt with Jakarta**: same pages, a fraction of the code | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

> ⚙️ From this topic on you need **Maven**. Read [SETUP.md](../SETUP.md) first if you haven't.
