function fib(n) {
    if (n < 2){ return 1; }
    //var l = fib(n-2); return l + fib(n-1);
    return fib(n-2) + fib(n-1)
}

var x = fib(4)
print(x)
