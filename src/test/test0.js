function Person(n) {
    this.setName(n);
    Person.prototype.count++;
}
Person.prototype.count = 0;
Person.prototype.setName = function (n) { this.name = n; }
function Student(n,s) {
    this.b = Person;
    this.b(n);
    delete this.b;
    this.studentid = s.toString();
}
Student.prototype = new Person;
var t = 100026.0;
var x = new Student("Joe Average", t++);
var y = new Student("John Doe", t);
y.setName("John Q. Doe");
print(x.name); // Joe Average
print(y.name); // John Q. Doe
print(y.studentid);  // 100027
print(x.count); // 3

var a = [];
a.push(1);
a.push("str");
print(a[0]);
print(a[1]);
print(a[1 + 1]);
print(a[0].length);
print(a[1].length);

function fac(n) {
    if (n == 0) {
        return 1;
    } else {
        return n * fac(n - 1);
    }
}
print(fac(0)); // 1
print(fac(1)); // 1
print(fac(2)); // 2
print(fac(3)); // 6

function h() {
    function g() {
        print("in g");
        h();
    }
    function h() {
        print("in h");
        g();
        function g() {
            print("in h.g");
        }
    }
    g();
}
h();

z = 1.0;
z = 2.0;
z = z + z;
print(z);

var t = (z < 5 && z < 7) ? 6 : "orz";
print(t);

var orz = undefined;
print(orz);

var sro = null;
print(sro);