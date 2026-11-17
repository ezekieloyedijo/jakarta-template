## Step 1

Download jdk 17 and above from adoptium

## Step 2: Download Maven Apache

I will put the file in my google drive aptech jakarta folder for my students sake. check the setup folder in the jakarta folder
Download unzip and put it in your C:drive
then copy the bin path and put it in your env path variable

## Step 3

Close and open VS code again and run mvn -v

## Step 4: Create and open your course folder

Copy the `starter-template` folder to your Desktop and rename it, e.g. `my-jakarta-course`.
In VS Code: File → Open Folder... → choose `my-jakarta-course`.
Open the terminal with Ctrl + ` (the key under Esc).

(First class only: instead of copying, build the folders by hand together.
Follow "Way A" in `starter-template/README.md`.)

## Step 5: Understand the folder structure

```
my-jakarta-course/
├── pom.xml              ← the parent: shared settings for every app (never changes)
└── 02-introduction-to-jakarta-ee/      ← one folder per topic
    ├── README.md  LESSON.md  QUIZ.md
    ├── example/         ← a small web app
    └── project/         ← another small web app
```

Inside every example/ and project/:

- `pom.xml`: the app's name, and "my parent is ../../pom.xml"
- `src/main/java/`: Java code (servlets)
- `src/main/webapp/`: public pages (HTML, CSS, JSP)
- `src/main/webapp/WEB-INF/`: private, the browser can't open it
- `target/`: made by Maven, never edit it

New topic? Copy `topic-template`, rename it (e.g. `05-session-tracking`),
and change `topicXX` to `topic05` in its two pom.xml files.

## Step 6: Run your first app

```
cd 02-introduction-to-jakarta-ee\example
mvn package cargo:run
```

Wait for: `Tomcat 10.1.34 started on port [8080]`
Open http://localhost:8080/ and http://localhost:8080/hello
Stop with Ctrl + C.
Changed something? Ctrl + C, then run `mvn package cargo:run` again.

(The first run downloads Tomcat, so it needs internet and takes a few minutes.)
