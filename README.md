# Kevlar

Password manager for the 5NTCM006C.1 Applied Cryptography module.

Team members:

<table>
  <tr>
    <th> Name </th>
    <th> Student ID </th>
    <th> UoW ID </th>
  </tr>
  <tr>
    <td> Dhiraj Wishal (Team Leader) </td>
    <td> 20200903 </td>
    <td> w1838836 </td>
  </tr>
  <tr>
    <td> Thulana Thidaswin </td>
    <td> 20200904 </td>
    <td> w1838837 </td>
  </tr>
  <tr>
    <td> Faizan Muthaliff </td>
    <td> - </td>
    <td> - </td>
  </tr>
</table>

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
