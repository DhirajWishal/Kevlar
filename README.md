# Kevlar

Password manager for the 5NTCM006C.1 Applied Cryptography module.

Team members:

| Name                        | Student ID | UoW ID   | IIT Email                 |
| --------------------------- | ---------- | -------- |---------------------------|
| Dhiraj Wishal (Team Leader) | 20200903   | w1838836 | wishal.20200903@iit.ac.lk |
| Thulana Thidaswin           | 20200904   | w1838837 | thulana.20200904@iit.ac.lk|
| Faizan Muthaliff            | 20200898   | w1838834 | faizan.20200898@iit.ac.lk |

## How to build and run

Before we prepare for the build, make sure to have the following dependencies,

1. [JDK](https://www.oracle.com/java/technologies/downloads/). We prefer sticking to the latest version.
2. [Python](https://www.python.org/downloads/). Again, we prefer the latest version.
3. [Cryptography](https://pypi.org/project/cryptography/) python module.

First things first, clone this repository to a local directory.

```bash
git clone https://github.com/DhirajWishal/Kevlar {SOURCE}
cd {SOURCE}
```

*Where `{SOURCE}` means the directory where the repository is cloned to.*

Once cloned, you need to open up the `/client` folder in [Intellij Idea](https://www.jetbrains.com/idea/), and the `/server` project in [Pycharm](https://www.jetbrains.com/pycharm/).
From there on, you first need to run the server project, then the client application.

Note: If you're running the application for the first time, you might have to configure the IDE(s).

## Disclaimer

The application is built and tested on the JDK version 17.0.1, and Python version 3.10.

## License

This project is licensed under [Apache2.0](https://www.apache.org/licenses/LICENSE-2.0).
