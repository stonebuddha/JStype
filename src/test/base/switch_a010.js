var i = 5;

var s = "";

switch(i) {
	default: s = s + "default;";
	case 5: s = s + "case 5;"; break;
	case 1: s = s + "case 1;";
}

print(s);
