function fac(n) {
    if (n == 0) {
        return 1;
    } else {
        return n * fac(n - 1);
    }
}
var a = fac(0);
print(a);
