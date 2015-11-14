function field() {
    return "bar";
}
function foo(obj, y) {
    var x = 1;
    x += y;
    obj[field()] += y;
}