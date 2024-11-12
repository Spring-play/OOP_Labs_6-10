#include <iostream>
#include <limits>
#include <cstdint>

using namespace std;

void printBinaryUnsignedChar(unsigned char value) {
    cout << "Двоичное представление символа: ";
    for (int i = 7; i >= 0; --i) {
        cout << ((value >> i) & 1);
    }
    cout << endl;
}

void printBinaryDouble(double value) {
    union {
        double d;
        uint64_t i;
    } u;
    u.d = value;
    for (int i = 63; i >= 0; --i) {
        cout << ((u.i >> i) & 1);
    }
    cout << endl;
}

unsigned char invertBitsUnsignedChar(unsigned char value, int el) {
    value ^= (1<<(el));
    return value;
}

double invertBitsDouble(double value,int el) {
    union {
        double d;
        uint64_t i;
    } u;
    u.d = value;
    u.i ^= (1ULL << (el));
    return u.d;
}

double inputDouble() {
    double value;
    while (true) { 
        cout << "Введите число типа double: ";
        cin >> value;
        if (cin.fail()) { 
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Некорректный ввод, попробуйте снова." << endl;
        } else {
            return value;
        }
    }
}

unsigned char inputUnsignedChar() {
    unsigned char value;
    while (true) { 
        cout << "Введите символ: ";
        cin >> value;
        if (cin.fail()) { 
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Некорректный ввод, попробуйте снова." << endl;
        } else {
            return value;
        }
    }
}

int main() {
    bool running = true;

    while (running) { 
        int choice;

        cout << "\nВыберите, что хотите инвертировать:\n";
        cout << "1. Инвертировать биты в символе (unsigned char)\n";
        cout << "2. Инвертировать биты в числе (double)\n";
        cout << "3. Выйти из программы\n";
        cout << "Введите ваш выбор: ";

        if (!(cin >> choice)) { 
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Некорректный ввод. Пожалуйста, повторите попытку." << endl;
        }

        switch (choice) {
            case 1: {
                unsigned char UnsignedCharValue = inputUnsignedChar();

                cout << "Начальное двоичное представление символа:\n";
                printBinaryUnsignedChar(UnsignedCharValue);

                int colich;
                std::cout << "введите сколько бит хотите изменить" << std::endl;
                std::cin >> colich;
                for (size_t i = 0; i < colich; i++)
                {
                    int elem;
                    std::cout << "введите бит который хотите изменить" << std::endl;
                    std::cin >> elem;
                    UnsignedCharValue = invertBitsUnsignedChar(UnsignedCharValue,elem);
                }

                cout << "\nДанные после инверсии битов в unsigned char:\n";
                printBinaryUnsignedChar(UnsignedCharValue);
                cout << "Значение символа после инверсии битов: " << UnsignedCharValue << endl;
                break;
            }
            case 2: {
                double doubleValue = inputDouble();

                cout << "Начальное двоичное представление числа double:\n";
                printBinaryDouble(doubleValue);

                int colich;
                std::cout << "введите сколько бит хотите изменить" << std::endl;
                std::cin >> colich;
                for (size_t i = 0; i < colich; i++)
                {
                    int el;
                    std::cout << "введите бит который хотите изменить" << std::endl;
                    std::cin >> el;
                    doubleValue = invertBitsDouble(doubleValue,el);
                }

                cout << "\nДанные после инверсии битов в double:\n";
                printBinaryDouble(doubleValue);
                cout << "Значение double после инверсии битов: " << doubleValue << endl;
                break;
            }
            case 3:
                running = false;
                cout << "Программа завершена.\n";
                break;
            default:
                cout << "Некорректный выбор, попробуйте снова.\n";
        }
    }

    return 0; 
}
