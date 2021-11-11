# Kevlar

Password manager for the 5NTCM006C.1 Applied Cryptography module.

Team members:

| Name                        | Student ID | UoW ID   |
| --------------------------- | ---------- | -------- |
| Dhiraj Wishal (Team Leader) | 20200903   | w1838836 |
| Thulana Thidaswin           | 20200904   | w1838837 |
| Faizan Muthaliff            | 20200xxx   | w1838xxx |

## How to build

Before we prepare for the build, make sure to have the following dependencies,

1. [Python 3.x](https://www.python.org/downloads/)
2. [GNU GCC](https://gcc.gnu.org/) compiler or [Clang](https://clang.llvm.org/) compiler.

First things first, clone this repository to a local directory.

```bash
git clone https://github.com/DhirajWishal/Kevlar {SOURCE}
cd {SOURCE}
```

*Where source means the directory where the repository is cloned to.*

From there, you just need to run the `Build.py` under the `/Scripts` directory.

```bash
python Scripts/Build.py
```

If the build was successful, you will get a set of `Makefile`s, from which you can build and run the application.

## License

This project is licensed under [Apache2.0](https://www.apache.org/licenses/LICENSE-2.0).
