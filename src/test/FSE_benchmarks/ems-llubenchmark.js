
function ourprint(x) {print(x);}

this['Module'] = Module = {};

// The environment setup code below is customized to use Module.
// *** Environment setup code ***

var ENVIRONMENT_IS_NODE = ENVIRONMENT_IS_WORKER = ENVIRONMENT_IS_WEB = false;
var ENVIRONMENT_IS_SHELL = true;
Module['print'] = ourprint;
if (typeof printErr != 'undefined') Module['printErr'] = printErr; // not present in v8 or older sm
// Polyfill over SpiderMonkey/V8 differences
if (typeof read != 'undefined') {
  Module['read'] = read;
} else {
  Module['read'] = function(f) { snarf(f) };
}

function globalEval(x) {
  //eval.call(null, x);
}

if (!Module['load'] == 'undefined' && Module['read']) {
  Module['load'] = function(f) {
    globalEval(Module['read'](f));
  };
}
if (!Module['print']) {
  Module['print'] = function(){};
}
if (!Module['printErr']) {
  Module['printErr'] = Module['print'];
}
if (!Module['arguments']) {
  Module['arguments'] = [];
}
// *** Environment setup code ***
// Closure helpers
Module.print = Module['print'];
Module.printErr = Module['printErr'];
// Callbacks
if (!Module['preRun']) Module['preRun'] = [];
if (!Module['postRun']) Module['postRun'] = [];
// === Auto-generated preamble library stuff ===
//========================================
// Runtime code shared with compiler
//========================================
var Runtime = {
  stackSave: function () {
    return STACKTOP;
  },
  stackRestore: function (stackTop) {
    STACKTOP = stackTop;
  },
  forceAlign: function (target, quantum) {
    quantum = quantum || 4;
    if (quantum == 1) return target;
    if (isNumber(target) && isNumber(quantum)) {
      return Math.ceil(target/quantum)*quantum;
    } else if (isNumber(quantum) && isPowerOfTwo(quantum)) {
      var logg = log2(quantum);
      return '((((' +target + ')+' + (quantum-1) + ')>>' + logg + ')<<' + logg + ')';
    }
    return 'Math.ceil((' + target + ')/' + quantum + ')*' + quantum;
  },
  isNumberType: function (type) {
    return type in Runtime.INT_TYPES || type in Runtime.FLOAT_TYPES;
  },
  isPointerType: function isPointerType(type) {
  return type[type.length-1] == '*';
},
  isStructType: function isStructType(type) {
  if (isPointerType(type)) return false;
  if (/^\[\d+\ x\ (.*)\]/.test(type)) return true; // [15 x ?] blocks. Like structs
  if (/<?{ ?[^}]* ?}>?/.test(type)) return true; // { i32, i8 } etc. - anonymous struct types
  // See comment in isStructPointerType()
  return type[0] == '%';
},
  INT_TYPES: {"i1":0,"i8":0,"i16":0,"i32":0,"i64":0},
  FLOAT_TYPES: {"float":0,"double":0},
  or64: function (x, y) {
    var l = (x | 0) | (y | 0);
    var h = (Math.round(x / 4294967296) | Math.round(y / 4294967296)) * 4294967296;
    return l + h;
  },
  and64: function (x, y) {
    var l = (x | 0) & (y | 0);
    var h = (Math.round(x / 4294967296) & Math.round(y / 4294967296)) * 4294967296;
    return l + h;
  },
  xor64: function (x, y) {
    var l = (x | 0) ^ (y | 0);
    var h = (Math.round(x / 4294967296) ^ Math.round(y / 4294967296)) * 4294967296;
    return l + h;
  },
  getNativeTypeSize: function (type, quantumSize) {
    if (Runtime.QUANTUM_SIZE == 1) return 1;
    var size = {
      '%i1': 1,
      '%i8': 1,
      '%i16': 2,
      '%i32': 4,
      '%i64': 8,
      "%float": 4,
      "%double": 8
    }['%'+type]; // add '%' since float and double confuse Closure compiler as keys, and also spidermonkey as a compiler will remove 's from '_i8' etc
    if (!size) {
      if (type.charAt(type.length-1) == '*') {
        size = Runtime.QUANTUM_SIZE; // A pointer
      } else if (type[0] == 'i') {
        var bits = parseInt(type.substr(1));
        assert(bits % 8 == 0);
        size = bits/8;
      }
    }
    return size;
  },
  getNativeFieldSize: function (type) {
    return Math.max(Runtime.getNativeTypeSize(type), Runtime.QUANTUM_SIZE);
  },
  dedup: function dedup(items, ident) {
  var seen = {};
  if (ident) {
    return items.filter(function(item) {
      if (seen[item[ident]]) return false;
      seen[item[ident]] = true;
      return true;
    });
  } else {
    return items.filter(function(item) {
      if (seen[item]) return false;
      seen[item] = true;
      return true;
    });
  }
},
  set: function set() {
  var args = typeof arguments[0] === 'object' ? arguments[0] : arguments;
  var ret = {};
  for (var i = 0; i < args.length; i++) {
    ret[args[i]] = 0;
  }
  return ret;
},
  calculateStructAlignment: function calculateStructAlignment(type) {
    type.flatSize = 0;
    type.alignSize = 0;
    var diffs = [];
    var prev = -1;
    type.flatIndexes = type.fields.map(function(field) {
      var size, alignSize;
      if (Runtime.isNumberType(field) || Runtime.isPointerType(field)) {
        size = Runtime.getNativeTypeSize(field); // pack char; char; in structs, also char[X]s.
        alignSize = size;
      } else if (Runtime.isStructType(field)) {
        size = Types.types[field].flatSize;
        alignSize = Types.types[field].alignSize;
      } else if (field[0] == 'b') {
        // bN, large number field, like a [N x i8]
        size = field.substr(1)|0;
        alignSize = 1;
      } else {
        throw 'Unclear type in struct: ' + field + ', in ' + type.name_ + ' :: ' + dump(Types.types[type.name_]);
      }
      alignSize = type.packed ? 1 : Math.min(alignSize, Runtime.QUANTUM_SIZE);
      type.alignSize = Math.max(type.alignSize, alignSize);
      var curr = Runtime.alignMemory(type.flatSize, alignSize); // if necessary, place this on aligned memory
      type.flatSize = curr + size;
      if (prev >= 0) {
        diffs.push(curr-prev);
      }
      prev = curr;
      return curr;
    });
    type.flatSize = Runtime.alignMemory(type.flatSize, type.alignSize);
    if (diffs.length == 0) {
      type.flatFactor = type.flatSize;
    } else if (Runtime.dedup(diffs).length == 1) {
      type.flatFactor = diffs[0];
    }
    type.needsFlattening = (type.flatFactor != 1);
    return type.flatIndexes;
  },
  generateStructInfo: function (struct, typeName, offset) {
    var type, alignment;
    if (typeName) {
      offset = offset || 0;
      type = (typeof Types === 'undefined' ? Runtime.typeInfo : Types.types)[typeName];
      if (!type) return null;
      if (type.fields.length != struct.length) {
        printErr('Number of named fields must match the type for ' + typeName + ': possibly duplicate struct names. Cannot return structInfo');
        return null;
      }
      alignment = type.flatIndexes;
    } else {
      var type = { fields: struct.map(function(item) { return item[0] }) };
      alignment = Runtime.calculateStructAlignment(type);
    }
    var ret = {
      __size__: type.flatSize
    };
    if (typeName) {
      struct.forEach(function(item, i) {
        if (typeof item === 'string') {
          ret[item] = alignment[i] + offset;
        } else {
          // embedded struct
          var key;
          for (var k in item) key = k;
          ret[key] = Runtime.generateStructInfo(item[key], type.fields[i], alignment[i]);
        }
      });
    } else {
      struct.forEach(function(item, i) {
        ret[item[1]] = alignment[i];
      });
    }
    return ret;
  },
  dynCall: function (sig, ptr, args) {
    if (args && args.length) {
      assert(args.length == sig.length-1);
      return FUNCTION_TABLE[ptr].apply(null, args);
    } else {
      assert(sig.length == 1);
      return FUNCTION_TABLE[ptr]();
    }
  },
  addFunction: function (func, sig) {
    //assert(sig); // TODO: support asm
    var table = FUNCTION_TABLE; // TODO: support asm
    var ret = table.length;
    table.push(func);
    table.push(0);
    return ret;
  },
  removeFunction: function (index) {
    var table = FUNCTION_TABLE; // TODO: support asm
    table[index] = null;
  },
  warnOnce: function (text) {
    if (!Runtime.warnOnce.shown) Runtime.warnOnce.shown = {};
    if (!Runtime.warnOnce.shown[text]) {
      Runtime.warnOnce.shown[text] = 1;
      Module.printErr(text);
    }
  },
  funcWrappers: {},
  getFuncWrapper: function (func, sig) {
    assert(sig);
    if (!Runtime.funcWrappers[func]) {
      Runtime.funcWrappers[func] = function() {
        Runtime.dynCall(sig, func, arguments);
      };
    }
    return Runtime.funcWrappers[func];
  },
  UTF8Processor: function () {
    var buffer = [];
    var needed = 0;
    this.processCChar = function (code) {
      code = code & 0xff;
      if (needed) {
        buffer.push(code);
        needed--;
      }
      if (buffer.length == 0) {
        if (code < 128) return String.fromCharCode(code);
        buffer.push(code);
        if (code > 191 && code < 224) {
          needed = 1;
        } else {
          needed = 2;
        }
        return '';
      }
      if (needed > 0) return '';
      var c1 = buffer[0];
      var c2 = buffer[1];
      var c3 = buffer[2];
      var ret;
      if (c1 > 191 && c1 < 224) {
        ret = String.fromCharCode(((c1 & 31) << 6) | (c2 & 63));
      } else {
        ret = String.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
      }
      buffer.length = 0;
      return ret;
    }
    this.processJSString = function(string) {
      string = unescape(encodeURIComponent(string));
      var ret = [];
      for (var i = 0; i < string.length; i++) {
        ret.push(string.charCodeAt(i));
      }
      return ret;
    }
  },
  stackAlloc: function (size) { var ret = STACKTOP;STACKTOP = (STACKTOP + size)|0;STACKTOP = ((((STACKTOP)+3)>>2)<<2);assert((STACKTOP|0) < (STACK_MAX|0)); return ret; },
  staticAlloc: function (size) { var ret = STATICTOP;STATICTOP = (STATICTOP + size)|0;STATICTOP = ((((STATICTOP)+3)>>2)<<2); if (STATICTOP >= TOTAL_MEMORY) enlargeMemory();; return ret; },
  alignMemory: function (size,quantum) { var ret = size = Math.ceil((size)/(quantum ? quantum : 4))*(quantum ? quantum : 4); return ret; },
  makeBigInt: function (low,high,unsigned) { var ret = (unsigned ? (((low)>>>(0))+(((high)>>>(0))*4294967296)) : (((low)>>>(0))+(((high)|(0))*4294967296))); return ret; },
  QUANTUM_SIZE: 4,
  __dummy__: 0
}
//========================================
// Runtime essentials
//========================================
var __THREW__ = 0; // Used in checking for thrown exceptions.
var setjmpId = 1; // Used in setjmp/longjmp
var setjmpLabels = {};
var ABORT = false;
var undef = 0;
// tempInt is used for 32-bit signed values or smaller. tempBigInt is used
// for 32-bit unsigned values or more than 32 bits. TODO: audit all uses of tempInt
var tempValue, tempInt, tempBigInt, tempInt2, tempBigInt2, tempPair, tempBigIntI, tempBigIntR, tempBigIntS, tempBigIntP, tempBigIntD;
var tempI64, tempI64b;
var tempRet0, tempRet1, tempRet2, tempRet3, tempRet4, tempRet5, tempRet6, tempRet7, tempRet8, tempRet9;
function abort(text) {
  //Module.print(text + ':\n' + (new Error).stack);
  print(text + ': ERROR');
  ABORT = true;
  throw "Assertion: " + text;
}
function assert(condition, text) {
  if (!condition) {
    abort('Assertion failed: ' + text);
  }
}
var globalScope = this;
// C calling interface. A convenient way to call C functions (in C files, or
// defined with extern "C").
//
// Note: LLVM optimizations can inline and remove functions, after which you will not be
//       able to call them. Closure can also do so. To avoid that, add your function to
//       the exports using something like
//
//         -s EXPORTED_FUNCTIONS='["_main", "_myfunc"]'
//
// @param ident      The name of the C function (note that C++ functions will be name-mangled - use extern "C")
// @param returnType The return type of the function, one of the JS types 'number', 'string' or 'array' (use 'number' for any C pointer, and
//                   'array' for JavaScript arrays and typed arrays).
// @param argTypes   An array of the types of arguments for the function (if there are no arguments, this can be ommitted). Types are as in returnType,
//                   except that 'array' is not possible (there is no way for us to know the length of the array)
// @param args       An array of the arguments to the function, as native JS values (as in returnType)
//                   Note that string arguments will be stored on the stack (the JS string will become a C string on the stack).
// @return           The return value, as a native JS value (as in returnType)
function ccall(ident, returnType, argTypes, args) {
  return ccallFunc(getCFunc(ident), returnType, argTypes, args);
}
Module["ccall"] = ccall;
// Returns the C function with a specified identifier (for C++, you need to do manual name mangling)
function getCFunc(ident) {
  try {
    var func = globalScope['Module']['_' + ident]; // closure exported function
    if (!func) func = eval('_' + ident); // explicit lookup
  } catch(e) {
  }
  assert(func, 'Cannot call unknown function ' + ident + ' (perhaps LLVM optimizations or closure removed it?)');
  return func;
}
// Internal function that does a C call using a function, not an identifier
function ccallFunc(func, returnType, argTypes, args) {
  var stack = 0;
  function toC(value, type) {
    if (type == 'string') {
      if (value === null || value === undefined || value === 0) return 0; // null string
      if (!stack) stack = Runtime.stackSave();
      var ret = Runtime.stackAlloc(value.length+1);
      writeStringToMemory(value, ret);
      return ret;
    } else if (type == 'array') {
      if (!stack) stack = Runtime.stackSave();
      var ret = Runtime.stackAlloc(value.length);
      writeArrayToMemory(value, ret);
      return ret;
    }
    return value;
  }
  function fromC(value, type) {
    if (type == 'string') {
      return Pointer_stringify(value);
    }
    assert(type != 'array');
    return value;
  }
  var i = 0;
  var cArgs = args ? args.map(function(arg) {
    return toC(arg, argTypes[i++]);
  }) : [];
  var ret = fromC(func.apply(null, cArgs), returnType);
  if (stack) Runtime.stackRestore(stack);
  return ret;
}
// Returns a native JS wrapper for a C function. This is similar to ccall, but
// returns a function you can call repeatedly in a normal way. For example:
//
//   var my_function = cwrap('my_c_function', 'number', ['number', 'number']);
//   alert(my_function(5, 22));
//   alert(my_function(99, 12));
//
function cwrap(ident, returnType, argTypes) {
  var func = getCFunc(ident);
  return function() {
    return ccallFunc(func, returnType, argTypes, Array.prototype.slice.call(arguments));
  }
}
Module["cwrap"] = cwrap;
// Sets a value in memory in a dynamic way at run-time. Uses the
// type data. This is the same as makeSetValue, except that
// makeSetValue is done at compile-time and generates the needed
// code then, whereas this function picks the right code at
// run-time.
// Note that setValue and getValue only do *aligned* writes and reads!
// Note that ccall uses JS types as for defining types, while setValue and
// getValue need LLVM types ('i8', 'i32') - this is a lower-level operation
function setValue(ptr, value, type, noSafe) {
  type = type || 'i8';
  if (type.charAt(type.length-1) === '*') type = 'i32'; // pointers are 32-bit
    switch(type) {
      case 'i1': HEAP8[(ptr)]=value; break;
      case 'i8': HEAP8[(ptr)]=value; break;
      case 'i16': HEAP16[((ptr)>>1)]=value; break;
      case 'i32': HEAP32[((ptr)>>2)]=value; break;
      case 'i64': (tempI64 = [value>>>0,Math.min(Math.floor((value)/4294967296), 4294967295)>>>0],HEAP32[((ptr)>>2)]=tempI64[0],HEAP32[(((ptr)+(4))>>2)]=tempI64[1]); break;
      case 'float': HEAPF32[((ptr)>>2)]=value; break;
      case 'double': (HEAPF64[(tempDoublePtr)>>3]=value,HEAP32[((ptr)>>2)]=HEAP32[((tempDoublePtr)>>2)],HEAP32[(((ptr)+(4))>>2)]=HEAP32[(((tempDoublePtr)+(4))>>2)]); break;
      default: abort('invalid type for setValue: ' + type);
    }
}
Module['setValue'] = setValue;
// Parallel to setValue.
function getValue(ptr, type, noSafe) {
  type = type || 'i8';
  if (type.charAt(type.length-1) === '*') type = 'i32'; // pointers are 32-bit
    switch(type) {
      case 'i1': return HEAP8[(ptr)];
      case 'i8': return HEAP8[(ptr)];
      case 'i16': return HEAP16[((ptr)>>1)];
      case 'i32': return HEAP32[((ptr)>>2)];
      case 'i64': return HEAP32[((ptr)>>2)];
      case 'float': return HEAPF32[((ptr)>>2)];
      case 'double': return (HEAP32[((tempDoublePtr)>>2)]=HEAP32[((ptr)>>2)],HEAP32[(((tempDoublePtr)+(4))>>2)]=HEAP32[(((ptr)+(4))>>2)],HEAPF64[(tempDoublePtr)>>3]);
      default: abort('invalid type for setValue: ' + type);
    }
  return null;
}
Module['getValue'] = getValue;
var ALLOC_NORMAL = 0; // Tries to use _malloc()
var ALLOC_STACK = 1; // Lives for the duration of the current function call
var ALLOC_STATIC = 2; // Cannot be freed
var ALLOC_NONE = 3; // Do not allocate
Module['ALLOC_NORMAL'] = ALLOC_NORMAL;
Module['ALLOC_STACK'] = ALLOC_STACK;
Module['ALLOC_STATIC'] = ALLOC_STATIC;
Module['ALLOC_NONE'] = ALLOC_NONE;
// allocate(): This is for internal use. You can use it yourself as well, but the interface
//             is a little tricky (see docs right below). The reason is that it is optimized
//             for multiple syntaxes to save space in generated code. So you should
//             normally not use allocate(), and instead allocate memory using _malloc(),
//             initialize it with setValue(), and so forth.
// @slab: An array of data, or a number. If a number, then the size of the block to allocate,
//        in *bytes* (note that this is sometimes confusing: the next parameter does not
//        affect this!)
// @types: Either an array of types, one for each byte (or 0 if no type at that position),
//         or a single type which is used for the entire block. This only matters if there
//         is initial data - if @slab is a number, then this does not matter at all and is
//         ignored.
// @allocator: How to allocate memory, see ALLOC_*
function allocate(slab, types, allocator, ptr) {
  var zeroinit, size;
  if (typeof slab === 'number') {
    zeroinit = true;
    size = slab;
  } else {
    zeroinit = false;
    size = slab.length;
  }
  var singleType = typeof types === 'string' ? types : null;
  var ret;
  if (allocator == ALLOC_NONE) {
    ret = ptr;
  } else {
    ret = [_malloc, Runtime.stackAlloc, Runtime.staticAlloc][allocator === undefined ? ALLOC_STATIC : allocator](Math.max(size, singleType ? 1 : types.length));
  }
  if (zeroinit) {
    var ptr = ret, stop;
    assert((ret & 3) == 0);
    stop = ret + (size & ~3);
    for (; ptr < stop; ptr += 4) {
      HEAP32[((ptr)>>2)]=0;
    }
    stop = ret + size;
    while (ptr < stop) {
      HEAP8[((ptr++)|0)]=0;
    }
    return ret;
  }
  if (singleType === 'i8') {
    HEAPU8.set(new Uint8Array(slab), ret);
    return ret;
  }
  var i = 0, type, typeSize, previousType;
  while (i < size) {
    var curr = slab[i];
    if (typeof curr === 'function') {
      curr = Runtime.getFunctionIndex(curr);
    }
    type = singleType || types[i];
    if (type === 0) {
      i++;
      continue;
    }
    assert(type, 'Must know what type to store in allocate!');
    if (type == 'i64') type = 'i32'; // special case: we have one i32 here, and one i32 later
    setValue(ret+i, curr, type);
    // no need to look up size unless type changes, so cache it
    if (previousType !== type) {
      typeSize = Runtime.getNativeTypeSize(type);
      previousType = type;
    }
    i += typeSize;
  }
  return ret;
}
Module['allocate'] = allocate;
function Pointer_stringify(ptr, /* optional */ length) {
  // Find the length, and check for UTF while doing so
  var hasUtf = false;
  var t;
  var i = 0;
  while (1) {
    t = HEAPU8[(((ptr)+(i))|0)];
    if (t >= 128) hasUtf = true;
    else if (t == 0 && !length) break;
    i++;
    if (length && i == length) break;
  }
  if (!length) length = i;
  var ret = '';
  if (!hasUtf) {
    var MAX_CHUNK = 1024; // split up into chunks, because .apply on a huge string can overflow the stack
    var curr;
    while (length > 0) {
      curr = String.fromCharCode.apply(String, HEAPU8.subarray(ptr, ptr + Math.min(length, MAX_CHUNK)));
      ret = ret ? ret + curr : curr;
      ptr += MAX_CHUNK;
      length -= MAX_CHUNK;
    }
    return ret;
  }
  var utf8 = new Runtime.UTF8Processor();
  for (i = 0; i < length; i++) {
    assert(ptr + i < TOTAL_MEMORY);
    t = HEAPU8[(((ptr)+(i))|0)];
    ret += utf8.processCChar(t);
  }
  return ret;
}
Module['Pointer_stringify'] = Pointer_stringify;
// Memory management
var PAGE_SIZE = 4096;
function alignMemoryPage(x) {
  return ((x+4095)>>12)<<12;
}
var HEAP;
var HEAP8, HEAPU8, HEAP16, HEAPU16, HEAP32, HEAPU32, HEAPF32, HEAPF64;
var STACK_ROOT, STACKTOP, STACK_MAX;
var STATICTOP;
function enlargeMemory() {
  abort('Cannot enlarge memory arrays. Either (1) compile with -s TOTAL_MEMORY=X with X higher than the current value, (2) compile with ALLOW_MEMORY_GROWTH which adjusts the size at runtime but prevents some optimizations, or (3) set Module.TOTAL_MEMORY before the program runs.');
}
var TOTAL_STACK = Module['TOTAL_STACK'] || 5242880;
var TOTAL_MEMORY = Module['TOTAL_MEMORY'] || 16777216;
var FAST_MEMORY = Module['FAST_MEMORY'] || 2097152;
// Initialize the runtime's memory
// check for full engine support (use string 'subarray' to avoid closure compiler confusion)
assert(!!Int32Array && !!Float64Array && !!(new Int32Array(1)['subarray']) && !!(new Int32Array(1)['set']),
       'Cannot fallback to non-typed array case: Code is too specialized');
var buffer = new ArrayBuffer(TOTAL_MEMORY);
HEAP8 = new Int8Array(buffer);
HEAP16 = new Int16Array(buffer);
HEAP32 = new Int32Array(buffer);
HEAPU8 = new Uint8Array(buffer);
HEAPU16 = new Uint16Array(buffer);
HEAPU32 = new Uint32Array(buffer);
HEAPF32 = new Float32Array(buffer);
HEAPF64 = new Float64Array(buffer);
// Endianness check (note: assumes compiler arch was little-endian)
HEAP32[0] = 255;
assert(HEAPU8[0] === 255 && HEAPU8[3] === 0, 'Typed arrays 2 must be run on a little-endian system');
Module['HEAP'] = HEAP;
Module['HEAP8'] = HEAP8;
Module['HEAP16'] = HEAP16;
Module['HEAP32'] = HEAP32;
Module['HEAPU8'] = HEAPU8;
Module['HEAPU16'] = HEAPU16;
Module['HEAPU32'] = HEAPU32;
Module['HEAPF32'] = HEAPF32;
Module['HEAPF64'] = HEAPF64;
STACK_ROOT = STACKTOP = Runtime.alignMemory(1);
STACK_MAX = TOTAL_STACK; // we lose a little stack here, but TOTAL_STACK is nice and round so use that as the max
var tempDoublePtr = Runtime.alignMemory(allocate(12, 'i8', ALLOC_STACK), 8);
assert(tempDoublePtr % 8 == 0);
function copyTempFloat(ptr) { // functions, because inlining this code increases code size too much
  HEAP8[tempDoublePtr] = HEAP8[ptr];
  HEAP8[tempDoublePtr+1] = HEAP8[ptr+1];
  HEAP8[tempDoublePtr+2] = HEAP8[ptr+2];
  HEAP8[tempDoublePtr+3] = HEAP8[ptr+3];
}
function copyTempDouble(ptr) {
  HEAP8[tempDoublePtr] = HEAP8[ptr];
  HEAP8[tempDoublePtr+1] = HEAP8[ptr+1];
  HEAP8[tempDoublePtr+2] = HEAP8[ptr+2];
  HEAP8[tempDoublePtr+3] = HEAP8[ptr+3];
  HEAP8[tempDoublePtr+4] = HEAP8[ptr+4];
  HEAP8[tempDoublePtr+5] = HEAP8[ptr+5];
  HEAP8[tempDoublePtr+6] = HEAP8[ptr+6];
  HEAP8[tempDoublePtr+7] = HEAP8[ptr+7];
}
STATICTOP = STACK_MAX;
assert(STATICTOP < TOTAL_MEMORY); // Stack must fit in TOTAL_MEMORY; allocations from here on may enlarge TOTAL_MEMORY
var nullString = allocate(intArrayFromString('(null)'), 'i8', ALLOC_STACK);
function callRuntimeCallbacks(callbacks) {
  while(callbacks.length > 0) {
    var callback = callbacks.shift();
    var func = callback.func;
    if (typeof func === 'number') {
      if (callback.arg === undefined) {
        Runtime.dynCall('v', func);
      } else {
        Runtime.dynCall('vi', func, [callback.arg]);
      }
    } else {
      func(callback.arg === undefined ? null : callback.arg);
    }
  }
}
var __ATINIT__ = []; // functions called during startup
var __ATMAIN__ = []; // functions called when main() is to be run
var __ATEXIT__ = []; // functions called during shutdown
function initRuntime() {
  callRuntimeCallbacks(__ATINIT__);
}
function preMain() {
  callRuntimeCallbacks(__ATMAIN__);
}
function exitRuntime() {
  callRuntimeCallbacks(__ATEXIT__);
}
// Tools
// This processes a JS string into a C-line array of numbers, 0-terminated.
// For LLVM-originating strings, see parser.js:parseLLVMString function
function intArrayFromString(stringy, dontAddNull, length /* optional */) {
  var ret = (new Runtime.UTF8Processor()).processJSString(stringy);
  if (length) {
    ret.length = length;
  }
  if (!dontAddNull) {
    ret.push(0);
  }
  return ret;
}
Module['intArrayFromString'] = intArrayFromString;
function intArrayToString(array) {
  var ret = [];
  for (var i = 0; i < array.length; i++) {
    var chr = array[i];
    if (chr > 0xFF) {
        assert(false, 'Character code ' + chr + ' (' + String.fromCharCode(chr) + ')  at offset ' + i + ' not in 0x00-0xFF.');
      chr &= 0xFF;
    }
    ret.push(String.fromCharCode(chr));
  }
  return ret.join('');
}
Module['intArrayToString'] = intArrayToString;
// Write a Javascript array to somewhere in the heap
function writeStringToMemory(string, buffer, dontAddNull) {
  var array = intArrayFromString(string, dontAddNull);
  var i = 0;
  while (i < array.length) {
    var chr = array[i];
    HEAP8[(((buffer)+(i))|0)]=chr
    i = i + 1;
  }
}
Module['writeStringToMemory'] = writeStringToMemory;
function writeArrayToMemory(array, buffer) {
  for (var i = 0; i < array.length; i++) {
    HEAP8[(((buffer)+(i))|0)]=array[i];
  }
}
Module['writeArrayToMemory'] = writeArrayToMemory;
function unSign(value, bits, ignore, sig) {
  if (value >= 0) {
    return value;
  }
  return bits <= 32 ? 2*Math.abs(1 << (bits-1)) + value // Need some trickery, since if bits == 32, we are right at the limit of the bits JS uses in bitshifts
                    : Math.pow(2, bits)         + value;
}
function reSign(value, bits, ignore, sig) {
  if (value <= 0) {
    return value;
  }
  var half = bits <= 32 ? Math.abs(1 << (bits-1)) // abs is needed if bits == 32
                        : Math.pow(2, bits-1);
  if (value >= half && (bits <= 32 || value > half)) { // for huge values, we can hit the precision limit and always get true here. so don't do that
                                                       // but, in general there is no perfect solution here. With 64-bit ints, we get rounding and errors
                                                       // TODO: In i64 mode 1, resign the two parts separately and safely
    value = -2*half + value; // Cannot bitshift half, as it may be at the limit of the bits JS uses in bitshifts
  }
  return value;
}
if (!Math.imul) Math.imul = function(a, b) {
  var ah  = a >>> 16;
  var al = a & 0xffff;
  var bh  = b >>> 16;
  var bl = b & 0xffff;
  return (al*bl + ((ah*bl + al*bh) << 16))|0;
};
// A counter of dependencies for calling run(). If we need to
// do asynchronous work before running, increment this and
// decrement it. Incrementing must happen in a place like
// PRE_RUN_ADDITIONS (used by emcc to add file preloading).
// Note that you can add dependencies in preRun, even though
// it happens right before run - run will be postponed until
// the dependencies are met.
var runDependencies = 0;
var runDependencyTracking = {};
var calledRun = false;
var runDependencyWatcher = null;
function addRunDependency(id) {
  runDependencies++;
  if (Module['monitorRunDependencies']) {
    Module['monitorRunDependencies'](runDependencies);
  }
  if (id) {
    assert(!runDependencyTracking[id]);
    runDependencyTracking[id] = 1;
    if (runDependencyWatcher === null && typeof setInterval !== 'undefined') {
      // Check for missing dependencies every few seconds
      runDependencyWatcher = setInterval(function() {
        var shown = false;
        for (var dep in runDependencyTracking) {
          if (!shown) {
            shown = true;
            Module.printErr('still waiting on run dependencies:');
          }
          Module.printErr('dependency: ' + dep);
        }
        if (shown) {
          Module.printErr('(end of list)');
        }
      }, 6000);
    }
  } else {
    Module.printErr('warning: run dependency added without ID');
  }
}
Module['addRunDependency'] = addRunDependency;
function removeRunDependency(id) {
  runDependencies--;
  if (Module['monitorRunDependencies']) {
    Module['monitorRunDependencies'](runDependencies);
  }
  if (id) {
    assert(runDependencyTracking[id]);
    delete runDependencyTracking[id];
  } else {
    Module.printErr('warning: run dependency removed without ID');
  }
  if (runDependencies == 0) {
    if (runDependencyWatcher !== null) {
      clearInterval(runDependencyWatcher);
      runDependencyWatcher = null;
    } 
    // If run has never been called, and we should call run (INVOKE_RUN is true, and Module.noInitialRun is not false)
    if (!calledRun && shouldRunNow) run();
  }
}
Module['removeRunDependency'] = removeRunDependency;
Module["preloadedImages"] = {}; // maps url to image data
Module["preloadedAudios"] = {}; // maps url to audio data
// === Body ===
assert(STATICTOP == STACK_MAX); assert(STACK_MAX == TOTAL_STACK);
STATICTOP += 1104;
assert(STATICTOP < TOTAL_MEMORY);
allocate([0,0,0,0,0,0,36,64,0,0,0,0,0,0,89,64,0,0,0,0,0,136,195,64,0,0,0,0,132,215,151,65,0,128,224,55,121,195,65,67,23,110,5,181,181,184,147,70,245,249,63,233,3,79,56,77,50,29,48,249,72,119,130,90,60,191,115,127,221,79,21,117], "i8", ALLOC_NONE, 5242880);
allocate(4, "i8", ALLOC_NONE, 5242952);
allocate(24, "i8", ALLOC_NONE, 5242956);
allocate([255,1,0,0], "i8", ALLOC_NONE, 5242980);
allocate([32,0,0,0], "i8", ALLOC_NONE, 5242984);
allocate([112,97,114,115,101,32,101,114,114,111,114,32,105,110,32,37,115,10,0] /* parse error in %s\0A */, "i8", ALLOC_NONE, 5242988);
allocate([84,104,105,115,32,98,101,110,99,104,109,97,114,107,32,109,111,100,105,102,105,101,100,32,116,111,32,110,111,116,32,117,115,101,32,104,97,114,100,32,99,111,100,101,100,32,112,111,111,108,32,97,108,108,111,99,97,116,105,111,110,33,10,0] /* This benchmark modif */, "i8", ALLOC_NONE, 5243008);
allocate([91,45,116,93,32,40,105,110,115,101,114,116,32,97,116,32,40,84,41,97,105,108,32,111,102,32,108,105,115,116,44,32,100,101,102,97,117,108,116,32,111,102,102,41,10,0] /* [-t] (insert at (T)a */, "i8", ALLOC_NONE, 5243072);
allocate([91,45,100,93,32,40,40,68,41,105,114,116,121,32,101,97,99,104,32,101,108,101,109,101,110,116,32,100,117,114,105,110,103,32,116,114,97,118,101,114,115,97,108,44,32,100,101,102,97,117,108,116,32,111,102,102,41,10,0] /* [-d] ((D)irty each e */, "i8", ALLOC_NONE, 5243120);
allocate([91,45,103,32,60,40,71,41,114,111,119,116,104,32,114,97,116,101,32,112,101,114,32,108,105,115,116,44,32,105,110,32,101,108,101,109,101,110,116,115,32,112,101,114,32,105,116,101,114,97,116,105,111,110,62,93,32,40,100,101,102,97,117,108,116,32,48,41,10,0] /* [-g _(G)rowth rate p */, "i8", ALLOC_NONE, 5243180);
allocate([91,45,115,32,60,40,83,41,105,122,101,32,111,102,32,101,108,101,109,101,110,116,62,93,32,40,100,101,102,97,117,108,116,32,51,50,32,98,121,116,101,115,41,10,0] /* [-s _(S)ize of eleme */, "i8", ALLOC_NONE, 5243252);
allocate([91,45,110,32,60,40,78,41,117,109,98,101,114,32,111,102,32,108,105,115,116,115,62,93,32,40,100,101,102,97,117,108,116,32,49,32,108,105,115,116,41,10,0] /* [-n _(N)umber of lis */, "i8", ALLOC_NONE, 5243300);
allocate([91,45,108,32,60,105,110,105,116,105,97,108,32,40,76,41,101,110,103,116,104,32,111,102,32,108,105,115,116,44,32,105,110,32,101,108,101,109,101,110,116,115,62,93,32,40,100,101,102,97,117,108,116,32,49,41,10,0] /* [-l _initial (L)engt */, "i8", ALLOC_NONE, 5243344);
allocate([110,117,109,32,97,108,108,111,99,97,116,101,100,32,37,100,10,0] /* num allocated %d\0A\ */, "i8", ALLOC_NONE, 5243404);
allocate([111,117,116,112,117,116,32,61,32,37,100,10,0] /* output = %d\0A\00 */, "i8", ALLOC_NONE, 5243424);
allocate([37,100,10,0] /* %d\0A\00 */, "i8", ALLOC_NONE, 5243440);
allocate([117,110,114,101,99,111,103,110,105,122,101,100,32,111,112,116,105,111,110,58,32,37,99,10,0] /* unrecognized option: */, "i8", ALLOC_NONE, 5243444);
allocate([45,105,32,60,110,117,109,98,101,114,32,111,102,32,40,73,41,116,101,114,97,116,105,111,110,115,62,10,0] /* -i _number of (I)ter */, "i8", ALLOC_NONE, 5243472);
allocate([37,115,58,10,0] /* %s:\0A\00 */, "i8", ALLOC_NONE, 5243504);
allocate(472, "i8", ALLOC_NONE, 5243512);
  var ERRNO_CODES={E2BIG:7,EACCES:13,EADDRINUSE:98,EADDRNOTAVAIL:99,EAFNOSUPPORT:97,EAGAIN:11,EALREADY:114,EBADF:9,EBADMSG:74,EBUSY:16,ECANCELED:125,ECHILD:10,ECONNABORTED:103,ECONNREFUSED:111,ECONNRESET:104,EDEADLK:35,EDESTADDRREQ:89,EDOM:33,EDQUOT:122,EEXIST:17,EFAULT:14,EFBIG:27,EHOSTUNREACH:113,EIDRM:43,EILSEQ:84,EINPROGRESS:115,EINTR:4,EINVAL:22,EIO:5,EISCONN:106,EISDIR:21,ELOOP:40,EMFILE:24,EMLINK:31,EMSGSIZE:90,EMULTIHOP:72,ENAMETOOLONG:36,ENETDOWN:100,ENETRESET:102,ENETUNREACH:101,ENFILE:23,ENOBUFS:105,ENODATA:61,ENODEV:19,ENOENT:2,ENOEXEC:8,ENOLCK:37,ENOLINK:67,ENOMEM:12,ENOMSG:42,ENOPROTOOPT:92,ENOSPC:28,ENOSR:63,ENOSTR:60,ENOSYS:38,ENOTCONN:107,ENOTDIR:20,ENOTEMPTY:39,ENOTRECOVERABLE:131,ENOTSOCK:88,ENOTSUP:95,ENOTTY:25,ENXIO:6,EOVERFLOW:75,EOWNERDEAD:130,EPERM:1,EPIPE:32,EPROTO:71,EPROTONOSUPPORT:93,EPROTOTYPE:91,ERANGE:34,EROFS:30,ESPIPE:29,ESRCH:3,ESTALE:116,ETIME:62,ETIMEDOUT:110,ETXTBSY:26,EWOULDBLOCK:11,EXDEV:18};
  function ___setErrNo(value) {
      // For convenient setting and returning of errno.
      if (!___setErrNo.ret) ___setErrNo.ret = allocate([0], 'i32', ALLOC_STATIC);
      HEAP32[((___setErrNo.ret)>>2)]=value
      return value;
    }
  var _stdin=allocate(1, "i32*", ALLOC_STACK);
  var _stdout=allocate(1, "i32*", ALLOC_STACK);
  var _stderr=allocate(1, "i32*", ALLOC_STACK);
  var __impure_ptr=allocate(1, "i32*", ALLOC_STACK);var FS={currentPath:"/",nextInode:2,streams:[null],checkStreams:function () {
        for (var i in FS.streams) if (FS.streams.hasOwnProperty(i)) assert(i >= 0 && i < FS.streams.length); // no keys not in dense span
        for (var i = 0; i < FS.streams.length; i++) assert(typeof FS.streams[i] == 'object'); // no non-null holes in dense span
      },ignorePermissions:true,joinPath:function (parts, forceRelative) {
        var ret = parts[0];
        for (var i = 1; i < parts.length; i++) {
          if (ret[ret.length-1] != '/') ret += '/';
          ret += parts[i];
        }
        if (forceRelative && ret[0] == '/') ret = ret.substr(1);
        return ret;
      },absolutePath:function (relative, base) {
        if (typeof relative !== 'string') return null;
        if (base === undefined) base = FS.currentPath;
        if (relative && relative[0] == '/') base = '';
        var full = base + '/' + relative;
        var parts = full.split('/').reverse();
        var absolute = [''];
        while (parts.length) {
          var part = parts.pop();
          if (part == '' || part == '.') {
            // Nothing.
          } else if (part == '..') {
            if (absolute.length > 1) absolute.pop();
          } else {
            absolute.push(part);
          }
        }
        return absolute.length == 1 ? '/' : absolute.join('/');
      },analyzePath:function (path, dontResolveLastLink, linksVisited) {
        var ret = {
          isRoot: false,
          exists: false,
          error: 0,
          name: null,
          path: null,
          object: null,
          parentExists: false,
          parentPath: null,
          parentObject: null
        };
        path = FS.absolutePath(path);
        if (path == '/') {
          ret.isRoot = true;
          ret.exists = ret.parentExists = true;
          ret.name = '/';
          ret.path = ret.parentPath = '/';
          ret.object = ret.parentObject = FS.root;
        } else if (path !== null) {
          linksVisited = linksVisited || 0;
          path = path.slice(1).split('/');
          var current = FS.root;
          var traversed = [''];
          while (path.length) {
            if (path.length == 1 && current.isFolder) {
              ret.parentExists = true;
              ret.parentPath = traversed.length == 1 ? '/' : traversed.join('/');
              ret.parentObject = current;
              ret.name = path[0];
            }
            var target = path.shift();
            if (!current.isFolder) {
              ret.error = ERRNO_CODES.ENOTDIR;
              break;
            } else if (!current.read) {
              ret.error = ERRNO_CODES.EACCES;
              break;
            } else if (!current.contents.hasOwnProperty(target)) {
              ret.error = ERRNO_CODES.ENOENT;
              break;
            }
            current = current.contents[target];
            if (current.link && !(dontResolveLastLink && path.length == 0)) {
              if (linksVisited > 40) { // Usual Linux SYMLOOP_MAX.
                ret.error = ERRNO_CODES.ELOOP;
                break;
              }
              var link = FS.absolutePath(current.link, traversed.join('/'));
              ret = FS.analyzePath([link].concat(path).join('/'),
                                   dontResolveLastLink, linksVisited + 1);
              return ret;
            }
            traversed.push(target);
            if (path.length == 0) {
              ret.exists = true;
              ret.path = traversed.join('/');
              ret.object = current;
            }
          }
        }
        return ret;
      },findObject:function (path, dontResolveLastLink) {
        FS.ensureRoot();
        var ret = FS.analyzePath(path, dontResolveLastLink);
        if (ret.exists) {
          return ret.object;
        } else {
          ___setErrNo(ret.error);
          return null;
        }
      },createObject:function (parent, name, properties, canRead, canWrite) {
        if (!parent) parent = '/';
        if (typeof parent === 'string') parent = FS.findObject(parent);
        if (!parent) {
          ___setErrNo(ERRNO_CODES.EACCES);
          throw new Error('Parent path must exist.');
        }
        if (!parent.isFolder) {
          ___setErrNo(ERRNO_CODES.ENOTDIR);
          throw new Error('Parent must be a folder.');
        }
        if (!parent.write && !FS.ignorePermissions) {
          ___setErrNo(ERRNO_CODES.EACCES);
          throw new Error('Parent folder must be writeable.');
        }
        if (!name || name == '.' || name == '..') {
          ___setErrNo(ERRNO_CODES.ENOENT);
          throw new Error('Name must not be empty.');
        }
        if (parent.contents.hasOwnProperty(name)) {
          ___setErrNo(ERRNO_CODES.EEXIST);
          throw new Error("Can't overwrite object.");
        }
        parent.contents[name] = {
          read: canRead === undefined ? true : canRead,
          write: canWrite === undefined ? false : canWrite,
          timestamp: Date.now(),
          inodeNumber: FS.nextInode++
        };
        for (var key in properties) {
          if (properties.hasOwnProperty(key)) {
            parent.contents[name][key] = properties[key];
          }
        }
        return parent.contents[name];
      },createFolder:function (parent, name, canRead, canWrite) {
        var properties = {isFolder: true, isDevice: false, contents: {}};
        return FS.createObject(parent, name, properties, canRead, canWrite);
      },createPath:function (parent, path, canRead, canWrite) {
        var current = FS.findObject(parent);
        if (current === null) throw new Error('Invalid parent.');
        path = path.split('/').reverse();
        while (path.length) {
          var part = path.pop();
          if (!part) continue;
          if (!current.contents.hasOwnProperty(part)) {
            FS.createFolder(current, part, canRead, canWrite);
          }
          current = current.contents[part];
        }
        return current;
      },createFile:function (parent, name, properties, canRead, canWrite) {
        properties.isFolder = false;
        return FS.createObject(parent, name, properties, canRead, canWrite);
      },createDataFile:function (parent, name, data, canRead, canWrite) {
        if (typeof data === 'string') {
          var dataArray = new Array(data.length);
          for (var i = 0, len = data.length; i < len; ++i) dataArray[i] = data.charCodeAt(i);
          data = dataArray;
        }
        var properties = {
          isDevice: false,
          contents: data.subarray ? data.subarray(0) : data // as an optimization, create a new array wrapper (not buffer) here, to help JS engines understand this object
        };
        return FS.createFile(parent, name, properties, canRead, canWrite);
      },createLazyFile:function (parent, name, url, canRead, canWrite) {
        if (typeof XMLHttpRequest !== 'undefined') {
          if (!ENVIRONMENT_IS_WORKER) throw 'Cannot do synchronous binary XHRs outside webworkers in modern browsers. Use --embed-file or --preload-file in emcc';
          // Lazy chunked Uint8Array (implements get and length from Uint8Array). Actual getting is abstracted away for eventual reuse.
          var LazyUint8Array = function(chunkSize, length) {
            this.length = length;
            this.chunkSize = chunkSize;
            this.chunks = []; // Loaded chunks. Index is the chunk number
          }
          LazyUint8Array.prototype.get = function(idx) {
            if (idx > this.length-1 || idx < 0) {
              return undefined;
            }
            var chunkOffset = idx % chunkSize;
            var chunkNum = Math.floor(idx / chunkSize);
            return this.getter(chunkNum)[chunkOffset];
          }
          LazyUint8Array.prototype.setDataGetter = function(getter) {
            this.getter = getter;
          }
          // Find length
          var xhr = new XMLHttpRequest();
          xhr.open('HEAD', url, false);
          xhr.send(null);
          if (!(xhr.status >= 200 && xhr.status < 300 || xhr.status === 304)) throw new Error("Couldn't load " + url + ". Status: " + xhr.status);
          var datalength = Number(xhr.getResponseHeader("Content-length"));
          var header;
          var hasByteServing = (header = xhr.getResponseHeader("Accept-Ranges")) && header === "bytes";
          var chunkSize = 1024*1024; // Chunk size in bytes
          if (!hasByteServing) chunkSize = datalength;
          // Function to get a range from the remote URL.
          var doXHR = (function(from, to) {
            if (from > to) throw new Error("invalid range (" + from + ", " + to + ") or no bytes requested!");
            if (to > datalength-1) throw new Error("only " + datalength + " bytes available! programmer error!");
            // TODO: Use mozResponseArrayBuffer, responseStream, etc. if available.
            var xhr = new XMLHttpRequest();
            xhr.open('GET', url, false);
            if (datalength !== chunkSize) xhr.setRequestHeader("Range", "bytes=" + from + "-" + to);
            // Some hints to the browser that we want binary data.
            if (typeof Uint8Array != 'undefined') xhr.responseType = 'arraybuffer';
            if (xhr.overrideMimeType) {
              xhr.overrideMimeType('text/plain; charset=x-user-defined');
            }
            xhr.send(null);
            if (!(xhr.status >= 200 && xhr.status < 300 || xhr.status === 304)) throw new Error("Couldn't load " + url + ". Status: " + xhr.status);
            if (xhr.response !== undefined) {
              return new Uint8Array(xhr.response || []);
            } else {
              return intArrayFromString(xhr.responseText || '', true);
            }
          });
          var lazyArray = new LazyUint8Array(chunkSize, datalength);
          lazyArray.setDataGetter(function(chunkNum) {
            var start = chunkNum * lazyArray.chunkSize;
            var end = (chunkNum+1) * lazyArray.chunkSize - 1; // including this byte
            end = Math.min(end, datalength-1); // if datalength-1 is selected, this is the last block
            if (typeof(lazyArray.chunks[chunkNum]) === "undefined") {
              lazyArray.chunks[chunkNum] = doXHR(start, end);
            }
            if (typeof(lazyArray.chunks[chunkNum]) === "undefined") throw new Error("doXHR failed!");
            return lazyArray.chunks[chunkNum];
          });
          var properties = { isDevice: false, contents: lazyArray };
        } else {
          var properties = { isDevice: false, url: url };
        }
        return FS.createFile(parent, name, properties, canRead, canWrite);
      },createPreloadedFile:function (parent, name, url, canRead, canWrite, onload, onerror, dontCreateFile) {
        Browser.init();
        var fullname = FS.joinPath([parent, name], true);
        function processData(byteArray) {
          function finish(byteArray) {
            if (!dontCreateFile) {
              FS.createDataFile(parent, name, byteArray, canRead, canWrite);
            }
            if (onload) onload();
            removeRunDependency('cp ' + fullname);
          }
          var handled = false;
          Module['preloadPlugins'].forEach(function(plugin) {
            if (handled) return;
            if (plugin['canHandle'](fullname)) {
              plugin['handle'](byteArray, fullname, finish, function() {
                if (onerror) onerror();
                removeRunDependency('cp ' + fullname);
              });
              handled = true;
            }
          });
          if (!handled) finish(byteArray);
        }
        addRunDependency('cp ' + fullname);
        if (typeof url == 'string') {
          Browser.asyncLoad(url, function(byteArray) {
            processData(byteArray);
          }, onerror);
        } else {
          processData(url);
        }
      },createLink:function (parent, name, target, canRead, canWrite) {
        var properties = {isDevice: false, link: target};
        return FS.createFile(parent, name, properties, canRead, canWrite);
      },createDevice:function (parent, name, input, output) {
        if (!(input || output)) {
          throw new Error('A device must have at least one callback defined.');
        }
        var ops = {isDevice: true, input: input, output: output};
        return FS.createFile(parent, name, ops, Boolean(input), Boolean(output));
      },forceLoadFile:function (obj) {
        if (obj.isDevice || obj.isFolder || obj.link || obj.contents) return true;
        var success = true;
        if (typeof XMLHttpRequest !== 'undefined') {
          throw new Error("Lazy loading should have been performed (contents set) in createLazyFile, but it was not. Lazy loading only works in web workers. Use --embed-file or --preload-file in emcc on the main thread.");
        } else if (Module['read']) {
          // Command-line.
          try {
            // WARNING: Can't read binary files in V8's d8 or tracemonkey's js, as
            //          read() will try to parse UTF8.
            obj.contents = intArrayFromString(Module['read'](obj.url), true);
          } catch (e) {
            success = false;
          }
        } else {
          throw new Error('Cannot load without read() or XMLHttpRequest.');
        }
        if (!success) ___setErrNo(ERRNO_CODES.EIO);
        return success;
      },ensureRoot:function () {
        if (FS.root) return;
        // The main file system tree. All the contents are inside this.
        FS.root = {
          read: true,
          write: true,
          isFolder: true,
          isDevice: false,
          timestamp: Date.now(),
          inodeNumber: 1,
          contents: {}
        };
      },init:function (input, output, error) {
        // Make sure we initialize only once.
        assert(!FS.init.initialized, 'FS.init was previously called. If you want to initialize later with custom parameters, remove any earlier calls (note that one is automatically added to the generated code)');
        FS.init.initialized = true;
        FS.ensureRoot();
        // Allow Module.stdin etc. to provide defaults, if none explicitly passed to us here
        input = input || Module['stdin'];
        output = output || Module['stdout'];
        error = error || Module['stderr'];
        // Default handlers.
        var stdinOverridden = true, stdoutOverridden = true, stderrOverridden = true;
        if (!input) {
          stdinOverridden = false;
          input = function() {
            if (!input.cache || !input.cache.length) {
              var result;
              if (typeof window != 'undefined' &&
                  typeof window.prompt == 'function') {
                // Browser.
                result = window.prompt('Input: ');
                if (result === null) result = String.fromCharCode(0); // cancel ==> EOF
              } else if (typeof readline == 'function') {
                // Command line.
                result = readline();
              }
              if (!result) result = '';
              input.cache = intArrayFromString(result + '\n', true);
            }
            return input.cache.shift();
          };
        }
        var utf8 = new Runtime.UTF8Processor();
        function simpleOutput(val) {
          if (val === null || val === 10) {
            output.printer(output.buffer.join(''));
            output.buffer = [];
          } else {
            output.buffer.push(utf8.processCChar(val));
          }
        }
        if (!output) {
          stdoutOverridden = false;
          output = simpleOutput;
        }
        if (!output.printer) output.printer = Module['print'];
        if (!output.buffer) output.buffer = [];
        if (!error) {
          stderrOverridden = false;
          error = simpleOutput;
        }
        if (!error.printer) error.printer = Module['print'];
        if (!error.buffer) error.buffer = [];
        // Create the temporary folder, if not already created
        try {
          FS.createFolder('/', 'tmp', true, true);
        } catch(e) {}
        // Create the I/O devices.
        var devFolder = FS.createFolder('/', 'dev', true, true);
        var stdin = FS.createDevice(devFolder, 'stdin', input);
        var stdout = FS.createDevice(devFolder, 'stdout', null, output);
        var stderr = FS.createDevice(devFolder, 'stderr', null, error);
        FS.createDevice(devFolder, 'tty', input, output);
        // Create default streams.
        FS.streams[1] = {
          path: '/dev/stdin',
          object: stdin,
          position: 0,
          isRead: true,
          isWrite: false,
          isAppend: false,
          isTerminal: !stdinOverridden,
          error: false,
          eof: false,
          ungotten: []
        };
        FS.streams[2] = {
          path: '/dev/stdout',
          object: stdout,
          position: 0,
          isRead: false,
          isWrite: true,
          isAppend: false,
          isTerminal: !stdoutOverridden,
          error: false,
          eof: false,
          ungotten: []
        };
        FS.streams[3] = {
          path: '/dev/stderr',
          object: stderr,
          position: 0,
          isRead: false,
          isWrite: true,
          isAppend: false,
          isTerminal: !stderrOverridden,
          error: false,
          eof: false,
          ungotten: []
        };
        assert(Math.max(_stdin, _stdout, _stderr) < 128); // make sure these are low, we flatten arrays with these
        HEAP32[((_stdin)>>2)]=1;
        HEAP32[((_stdout)>>2)]=2;
        HEAP32[((_stderr)>>2)]=3;
        // Other system paths
        FS.createPath('/', 'dev/shm/tmp', true, true); // temp files
        // Newlib initialization
        for (var i = FS.streams.length; i < Math.max(_stdin, _stdout, _stderr) + 4; i++) {
          FS.streams[i] = null; // Make sure to keep FS.streams dense
        }
        FS.streams[_stdin] = FS.streams[1];
        FS.streams[_stdout] = FS.streams[2];
        FS.streams[_stderr] = FS.streams[3];
        FS.checkStreams();
        assert(FS.streams.length < 1024); // at this early stage, we should not have a large set of file descriptors - just a few
        allocate([ allocate(
          [0, 0, 0, 0, _stdin, 0, 0, 0, _stdout, 0, 0, 0, _stderr, 0, 0, 0],
          'void*', ALLOC_STATIC) ], 'void*', ALLOC_NONE, __impure_ptr);
      },quit:function () {
        if (!FS.init.initialized) return;
        // Flush any partially-printed lines in stdout and stderr. Careful, they may have been closed
        if (FS.streams[2] && FS.streams[2].object.output.buffer.length > 0) FS.streams[2].object.output(10);
        if (FS.streams[3] && FS.streams[3].object.output.buffer.length > 0) FS.streams[3].object.output(10);
      },standardizePath:function (path) {
        if (path.substr(0, 2) == './') path = path.substr(2);
        return path;
      },deleteFile:function (path) {
        path = FS.analyzePath(path);
        if (!path.parentExists || !path.exists) {
          throw 'Invalid path ' + path;
        }
        delete path.parentObject.contents[path.name];
      }};
  function _pwrite(fildes, buf, nbyte, offset) {
      // ssize_t pwrite(int fildes, const void *buf, size_t nbyte, off_t offset);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/write.html
      var stream = FS.streams[fildes];
      if (!stream || stream.object.isDevice) {
        ___setErrNo(ERRNO_CODES.EBADF);
        return -1;
      } else if (!stream.isWrite) {
        ___setErrNo(ERRNO_CODES.EACCES);
        return -1;
      } else if (stream.object.isFolder) {
        ___setErrNo(ERRNO_CODES.EISDIR);
        return -1;
      } else if (nbyte < 0 || offset < 0) {
        ___setErrNo(ERRNO_CODES.EINVAL);
        return -1;
      } else {
        var contents = stream.object.contents;
        while (contents.length < offset) contents.push(0);
        for (var i = 0; i < nbyte; i++) {
          contents[offset + i] = HEAPU8[(((buf)+(i))|0)];
        }
        stream.object.timestamp = Date.now();
        return i;
      }
    }function _write(fildes, buf, nbyte) {
      // ssize_t write(int fildes, const void *buf, size_t nbyte);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/write.html
      var stream = FS.streams[fildes];
      if (!stream) {
        ___setErrNo(ERRNO_CODES.EBADF);
        return -1;
      } else if (!stream.isWrite) {
        ___setErrNo(ERRNO_CODES.EACCES);
        return -1;
      } else if (nbyte < 0) {
        ___setErrNo(ERRNO_CODES.EINVAL);
        return -1;
      } else {
        if (stream.object.isDevice) {
          if (stream.object.output) {
            for (var i = 0; i < nbyte; i++) {
              try {
                stream.object.output(HEAP8[(((buf)+(i))|0)]);
              } catch (e) {
                ___setErrNo(ERRNO_CODES.EIO);
                return -1;
              }
            }
            stream.object.timestamp = Date.now();
            return i;
          } else {
            ___setErrNo(ERRNO_CODES.ENXIO);
            return -1;
          }
        } else {
          var bytesWritten = _pwrite(fildes, buf, nbyte, stream.position);
          if (bytesWritten != -1) stream.position += bytesWritten;
          return bytesWritten;
        }
      }
    }function _fwrite(ptr, size, nitems, stream) {
      // size_t fwrite(const void *restrict ptr, size_t size, size_t nitems, FILE *restrict stream);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/fwrite.html
      var bytesToWrite = nitems * size;
      if (bytesToWrite == 0) return 0;
      var bytesWritten = _write(stream, ptr, bytesToWrite);
      if (bytesWritten == -1) {
        if (FS.streams[stream]) FS.streams[stream].error = true;
        return 0;
      } else {
        return Math.floor(bytesWritten / size);
      }
    }
  function _strlen(ptr) {
      ptr = ptr|0;
      var curr = 0;
      curr = ptr;
      while (HEAP8[(curr)]|0 != 0) {
        curr = (curr + 1)|0;
      }
      return (curr - ptr)|0;
    }
  function __reallyNegative(x) {
      return x < 0 || (x === 0 && (1/x) === -Infinity);
    }function __formatString(format, varargs) {
      var textIndex = format;
      var argIndex = 0;
      function getNextArg(type) {
        // NOTE: Explicitly ignoring type safety. Otherwise this fails:
        //       int x = 4; printf("%c\n", (char)x);
        var ret;
        if (type === 'double') {
          ret = (HEAP32[((tempDoublePtr)>>2)]=HEAP32[(((varargs)+(argIndex))>>2)],HEAP32[(((tempDoublePtr)+(4))>>2)]=HEAP32[(((varargs)+((argIndex)+(4)))>>2)],HEAPF64[(tempDoublePtr)>>3]);
        } else if (type == 'i64') {
          ret = [HEAP32[(((varargs)+(argIndex))>>2)],
                 HEAP32[(((varargs)+(argIndex+4))>>2)]];
        } else {
          type = 'i32'; // varargs are always i32, i64, or double
          ret = HEAP32[(((varargs)+(argIndex))>>2)];
        }
        argIndex += Runtime.getNativeFieldSize(type);
        return ret;
      }
      var ret = [];
      var curr, next, currArg;
      while(1) {
        var startTextIndex = textIndex;
        curr = HEAP8[(textIndex)];
        if (curr === 0) break;
        next = HEAP8[((textIndex+1)|0)];
        if (curr == 37) {
          // Handle flags.
          var flagAlwaysSigned = false;
          var flagLeftAlign = false;
          var flagAlternative = false;
          var flagZeroPad = false;
          flagsLoop: while (1) {
            switch (next) {
              case 43:
                flagAlwaysSigned = true;
                break;
              case 45:
                flagLeftAlign = true;
                break;
              case 35:
                flagAlternative = true;
                break;
              case 48:
                if (flagZeroPad) {
                  break flagsLoop;
                } else {
                  flagZeroPad = true;
                  break;
                }
              default:
                break flagsLoop;
            }
            textIndex++;
            next = HEAP8[((textIndex+1)|0)];
          }
          // Handle width.
          var width = 0;
          if (next == 42) {
            width = getNextArg('i32');
            textIndex++;
            next = HEAP8[((textIndex+1)|0)];
          } else {
            while (next >= 48 && next <= 57) {
              width = width * 10 + (next - 48);
              textIndex++;
              next = HEAP8[((textIndex+1)|0)];
            }
          }
          // Handle precision.
          var precisionSet = false;
          if (next == 46) {
            var precision = 0;
            precisionSet = true;
            textIndex++;
            next = HEAP8[((textIndex+1)|0)];
            if (next == 42) {
              precision = getNextArg('i32');
              textIndex++;
            } else {
              while(1) {
                var precisionChr = HEAP8[((textIndex+1)|0)];
                if (precisionChr < 48 ||
                    precisionChr > 57) break;
                precision = precision * 10 + (precisionChr - 48);
                textIndex++;
              }
            }
            next = HEAP8[((textIndex+1)|0)];
          } else {
            var precision = 6; // Standard default.
          }
          // Handle integer sizes. WARNING: These assume a 32-bit architecture!
          var argSize;
          switch (String.fromCharCode(next)) {
            case 'h':
              var nextNext = HEAP8[((textIndex+2)|0)];
              if (nextNext == 104) {
                textIndex++;
                argSize = 1; // char (actually i32 in varargs)
              } else {
                argSize = 2; // short (actually i32 in varargs)
              }
              break;
            case 'l':
              var nextNext = HEAP8[((textIndex+2)|0)];
              if (nextNext == 108) {
                textIndex++;
                argSize = 8; // long long
              } else {
                argSize = 4; // long
              }
              break;
            case 'L': // long long
            case 'q': // int64_t
            case 'j': // intmax_t
              argSize = 8;
              break;
            case 'z': // size_t
            case 't': // ptrdiff_t
            case 'I': // signed ptrdiff_t or unsigned size_t
              argSize = 4;
              break;
            default:
              argSize = null;
          }
          if (argSize) textIndex++;
          next = HEAP8[((textIndex+1)|0)];
          // Handle type specifier.
          switch (String.fromCharCode(next)) {
            case 'd': case 'i': case 'u': case 'o': case 'x': case 'X': case 'p': {
              // Integer.
              var signed = next == 100 || next == 105;
              argSize = argSize || 4;
              var currArg = getNextArg('i' + (argSize * 8));
              var origArg = currArg;
              var argText;
              // Flatten i64-1 [low, high] into a (slightly rounded) double
              if (argSize == 8) {
                currArg = Runtime.makeBigInt(currArg[0], currArg[1], next == 117);
              }
              // Truncate to requested size.
              if (argSize <= 4) {
                var limit = Math.pow(256, argSize) - 1;
                currArg = (signed ? reSign : unSign)(currArg & limit, argSize * 8);
              }
              // Format the number.
              var currAbsArg = Math.abs(currArg);
              var prefix = '';
              if (next == 100 || next == 105) {
                if (argSize == 8 && i64Math) argText = i64Math.stringify(origArg[0], origArg[1], null); else
                argText = reSign(currArg, 8 * argSize, 1).toString(10);
              } else if (next == 117) {
                if (argSize == 8 && i64Math) argText = i64Math.stringify(origArg[0], origArg[1], true); else
                argText = unSign(currArg, 8 * argSize, 1).toString(10);
                currArg = Math.abs(currArg);
              } else if (next == 111) {
                argText = (flagAlternative ? '0' : '') + currAbsArg.toString(8);
              } else if (next == 120 || next == 88) {
                prefix = flagAlternative ? '0x' : '';
                if (argSize == 8 && i64Math) {
                  if (origArg[1]) {
                    argText = (origArg[1]>>>0).toString(16);
                    var lower = (origArg[0]>>>0).toString(16);
                    while (lower.length < 8) lower = '0' + lower;
                    argText += lower;
                  } else {
                    argText = (origArg[0]>>>0).toString(16);
                  }
                } else
                if (currArg < 0) {
                  // Represent negative numbers in hex as 2's complement.
                  currArg = -currArg;
                  argText = (currAbsArg - 1).toString(16);
                  var buffer = [];
                  for (var i = 0; i < argText.length; i++) {
                    buffer.push((0xF - parseInt(argText[i], 16)).toString(16));
                  }
                  argText = buffer.join('');
                  while (argText.length < argSize * 2) argText = 'f' + argText;
                } else {
                  argText = currAbsArg.toString(16);
                }
                if (next == 88) {
                  prefix = prefix.toUpperCase();
                  argText = argText.toUpperCase();
                }
              } else if (next == 112) {
                if (currAbsArg === 0) {
                  argText = '(nil)';
                } else {
                  prefix = '0x';
                  argText = currAbsArg.toString(16);
                }
              }
              if (precisionSet) {
                while (argText.length < precision) {
                  argText = '0' + argText;
                }
              }
              // Add sign if needed
              if (flagAlwaysSigned) {
                if (currArg < 0) {
                  prefix = '-' + prefix;
                } else {
                  prefix = '+' + prefix;
                }
              }
              // Add padding.
              while (prefix.length + argText.length < width) {
                if (flagLeftAlign) {
                  argText += ' ';
                } else {
                  if (flagZeroPad) {
                    argText = '0' + argText;
                  } else {
                    prefix = ' ' + prefix;
                  }
                }
              }
              // Insert the result into the buffer.
              argText = prefix + argText;
              argText.split('').forEach(function(chr) {
                ret.push(chr.charCodeAt(0));
              });
              break;
            }
            case 'f': case 'F': case 'e': case 'E': case 'g': case 'G': {
              // Float.
              var currArg = getNextArg('double');
              var argText;
              if (isNaN(currArg)) {
                argText = 'nan';
                flagZeroPad = false;
              } else if (!isFinite(currArg)) {
                argText = (currArg < 0 ? '-' : '') + 'inf';
                flagZeroPad = false;
              } else {
                var isGeneral = false;
                var effectivePrecision = Math.min(precision, 20);
                // Convert g/G to f/F or e/E, as per:
                // http://pubs.opengroup.org/onlinepubs/9699919799/functions/printf.html
                if (next == 103 || next == 71) {
                  isGeneral = true;
                  precision = precision || 1;
                  var exponent = parseInt(currArg.toExponential(effectivePrecision).split('e')[1], 10);
                  if (precision > exponent && exponent >= -4) {
                    next = ((next == 103) ? 'f' : 'F').charCodeAt(0);
                    precision -= exponent + 1;
                  } else {
                    next = ((next == 103) ? 'e' : 'E').charCodeAt(0);
                    precision--;
                  }
                  effectivePrecision = Math.min(precision, 20);
                }
                if (next == 101 || next == 69) {
                  argText = currArg.toExponential(effectivePrecision);
                  // Make sure the exponent has at least 2 digits.
                  if (/[eE][-+]\d$/.test(argText)) {
                    argText = argText.slice(0, -1) + '0' + argText.slice(-1);
                  }
                } else if (next == 102 || next == 70) {
                  argText = currArg.toFixed(effectivePrecision);
                  if (currArg === 0 && __reallyNegative(currArg)) {
                    argText = '-' + argText;
                  }
                }
                var parts = argText.split('e');
                if (isGeneral && !flagAlternative) {
                  // Discard trailing zeros and periods.
                  while (parts[0].length > 1 && parts[0].indexOf('.') != -1 &&
                         (parts[0].slice(-1) == '0' || parts[0].slice(-1) == '.')) {
                    parts[0] = parts[0].slice(0, -1);
                  }
                } else {
                  // Make sure we have a period in alternative mode.
                  if (flagAlternative && argText.indexOf('.') == -1) parts[0] += '.';
                  // Zero pad until required precision.
                  while (precision > effectivePrecision++) parts[0] += '0';
                }
                argText = parts[0] + (parts.length > 1 ? 'e' + parts[1] : '');
                // Capitalize 'E' if needed.
                if (next == 69) argText = argText.toUpperCase();
                // Add sign.
                if (flagAlwaysSigned && currArg >= 0) {
                  argText = '+' + argText;
                }
              }
              // Add padding.
              while (argText.length < width) {
                if (flagLeftAlign) {
                  argText += ' ';
                } else {
                  if (flagZeroPad && (argText[0] == '-' || argText[0] == '+')) {
                    argText = argText[0] + '0' + argText.slice(1);
                  } else {
                    argText = (flagZeroPad ? '0' : ' ') + argText;
                  }
                }
              }
              // Adjust case.
              if (next < 97) argText = argText.toUpperCase();
              // Insert the result into the buffer.
              argText.split('').forEach(function(chr) {
                ret.push(chr.charCodeAt(0));
              });
              break;
            }
            case 's': {
              // String.
              var arg = getNextArg('i8*') || nullString;
              var argLength = _strlen(arg);
              if (precisionSet) argLength = Math.min(argLength, precision);
              if (!flagLeftAlign) {
                while (argLength < width--) {
                  ret.push(32);
                }
              }
              for (var i = 0; i < argLength; i++) {
                ret.push(HEAPU8[((arg++)|0)]);
              }
              if (flagLeftAlign) {
                while (argLength < width--) {
                  ret.push(32);
                }
              }
              break;
            }
            case 'c': {
              // Character.
              if (flagLeftAlign) ret.push(getNextArg('i8'));
              while (--width > 0) {
                ret.push(32);
              }
              if (!flagLeftAlign) ret.push(getNextArg('i8'));
              break;
            }
            case 'n': {
              // Write the length written so far to the next parameter.
              var ptr = getNextArg('i32*');
              HEAP32[((ptr)>>2)]=ret.length
              break;
            }
            case '%': {
              // Literal percent sign.
              ret.push(curr);
              break;
            }
            default: {
              // Unknown specifiers remain untouched.
              for (var i = startTextIndex; i < textIndex + 2; i++) {
                ret.push(HEAP8[(i)]);
              }
            }
          }
          textIndex += 2;
          // TODO: Support a/A (hex float) and m (last error) specifiers.
          // TODO: Support %1${specifier} for arg selection.
        } else {
          ret.push(curr);
          textIndex += 1;
        }
      }
      return ret;
    }function _fprintf(stream, format, varargs) {
      // int fprintf(FILE *restrict stream, const char *restrict format, ...);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/printf.html
      var result = __formatString(format, varargs);
      var stack = Runtime.stackSave();
      var ret = _fwrite(allocate(result, 'i8', ALLOC_STACK), 1, result.length, stream);
      Runtime.stackRestore(stack);
      return ret;
    }function _printf(format, varargs) {
      // int printf(const char *restrict format, ...);
      // http://pubs.opengroup.org/onlinepubs/000095399/functions/printf.html
      var stdout = HEAP32[((_stdout)>>2)];
      return _fprintf(stdout, format, varargs);
    }
  function _isspace(chr) {
      return chr in { 32: 0, 9: 0, 10: 0, 11: 0, 12: 0, 13: 0 };
    }function __parseInt(str, endptr, base, min, max, bits, unsign) {
      // Skip space.
      while (_isspace(HEAP8[(str)])) str++;
      // Check for a plus/minus sign.
      var multiplier = 1;
      if (HEAP8[(str)] == 45) {
        multiplier = -1;
        str++;
      } else if (HEAP8[(str)] == 43) {
        str++;
      }
      // Find base.
      var finalBase = base;
      if (!finalBase) {
        if (HEAP8[(str)] == 48) {
          if (HEAP8[((str+1)|0)] == 120 ||
              HEAP8[((str+1)|0)] == 88) {
            finalBase = 16;
            str += 2;
          } else {
            finalBase = 8;
            str++;
          }
        }
      }
      if (!finalBase) finalBase = 10;
      // Get digits.
      var chr;
      var ret = 0;
      while ((chr = HEAP8[(str)]) != 0) {
        var digit = parseInt(String.fromCharCode(chr), finalBase);
        if (isNaN(digit)) {
          break;
        } else {
          ret = ret * finalBase + digit;
          str++;
        }
      }
      // Apply sign.
      ret *= multiplier;
      // Set end pointer.
      if (endptr) {
        HEAP32[((endptr)>>2)]=str
      }
      // Unsign if needed.
      if (unsign) {
        if (Math.abs(ret) > max) {
          ret = max;
          ___setErrNo(ERRNO_CODES.ERANGE);
        } else {
          ret = unSign(ret, bits);
        }
      }
      // Validate range.
      if (ret > max || ret < min) {
        ret = ret > max ? max : min;
        ___setErrNo(ERRNO_CODES.ERANGE);
      }
      if (bits == 64) {
        return tempRet0 = Math.min(Math.floor((ret)/4294967296), 4294967295)>>>0,ret>>>0;
      }
      return ret;
    }function _strtol(str, endptr, base) {
      return __parseInt(str, endptr, base, -2147483648, 2147483647, 32);  // LONG_MIN, LONG_MAX.
    }function _atoi(ptr) {
      return _strtol(ptr, null, 10);
    }
  function _abort() {
      ABORT = true;
      throw 'abort() at ' + (new Error().stack);
    }
  function ___errno_location() {
      return ___setErrNo.ret;
    }var ___errno=___errno_location;
  function _memcpy(dest, src, num) {
      dest = dest|0; src = src|0; num = num|0;
      var ret = 0;
      ret = dest|0;
      if ((dest&3) == (src&3)) {
        while (dest & 3) {
          if ((num|0) == 0) return ret|0;
          HEAP8[(dest)]=HEAP8[(src)];
          dest = (dest+1)|0;
          src = (src+1)|0;
          num = (num-1)|0;
        }
        while ((num|0) >= 4) {
          HEAP32[((dest)>>2)]=HEAP32[((src)>>2)];
          dest = (dest+4)|0;
          src = (src+4)|0;
          num = (num-4)|0;
        }
      }
      while ((num|0) > 0) {
        HEAP8[(dest)]=HEAP8[(src)];
        dest = (dest+1)|0;
        src = (src+1)|0;
        num = (num-1)|0;
      }
      return ret|0;
    }var _llvm_memcpy_p0i8_p0i8_i32=_memcpy;
  function _sysconf(name) {
      // long sysconf(int name);
      // http://pubs.opengroup.org/onlinepubs/009695399/functions/sysconf.html
      switch(name) {
        case 8: return PAGE_SIZE;
        case 54:
        case 56:
        case 21:
        case 61:
        case 63:
        case 22:
        case 67:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 69:
        case 28:
        case 101:
        case 70:
        case 71:
        case 29:
        case 30:
        case 199:
        case 75:
        case 76:
        case 32:
        case 43:
        case 44:
        case 80:
        case 46:
        case 47:
        case 45:
        case 48:
        case 49:
        case 42:
        case 82:
        case 33:
        case 7:
        case 108:
        case 109:
        case 107:
        case 112:
        case 119:
        case 121:
          return 200809;
        case 13:
        case 104:
        case 94:
        case 95:
        case 34:
        case 35:
        case 77:
        case 81:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 94:
        case 95:
        case 110:
        case 111:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 120:
        case 40:
        case 16:
        case 79:
        case 19:
          return -1;
        case 92:
        case 93:
        case 5:
        case 72:
        case 6:
        case 74:
        case 92:
        case 93:
        case 96:
        case 97:
        case 98:
        case 99:
        case 102:
        case 103:
        case 105:
          return 1;
        case 38:
        case 66:
        case 50:
        case 51:
        case 4:
          return 1024;
        case 15:
        case 64:
        case 41:
          return 32;
        case 55:
        case 37:
        case 17:
          return 2147483647;
        case 18:
        case 1:
          return 47839;
        case 59:
        case 57:
          return 99;
        case 68:
        case 58:
          return 2048;
        case 0: return 2097152;
        case 3: return 65536;
        case 14: return 32768;
        case 73: return 32767;
        case 39: return 16384;
        case 60: return 1000;
        case 106: return 700;
        case 52: return 256;
        case 62: return 255;
        case 2: return 100;
        case 65: return 64;
        case 36: return 20;
        case 100: return 16;
        case 20: return 6;
        case 53: return 4;
        case 10: return 1;
      }
      ___setErrNo(ERRNO_CODES.EINVAL);
      return -1;
    }
  function _time(ptr) {
      var ret = Math.floor(Date.now()/1000);
      if (ptr) {
        HEAP32[((ptr)>>2)]=ret
      }
      return ret;
    }
  function _sbrk(bytes) {
      // Implement a Linux-like 'memory area' for our 'process'.
      // Changes the size of the memory area by |bytes|; returns the
      // address of the previous top ('break') of the memory area
      // We need to make sure no one else allocates unfreeable memory!
      // We must control this entirely. So we don't even need to do
      // unfreeable allocations - the HEAP is ours, from STATICTOP up.
      // TODO: We could in theory slice off the top of the HEAP when
      //       sbrk gets a negative increment in |bytes|...
      var self = _sbrk;
      if (!self.called) {
        STATICTOP = alignMemoryPage(STATICTOP); // make sure we start out aligned
        self.called = true;
        _sbrk.DYNAMIC_START = STATICTOP;
      }
      var ret = STATICTOP;
      if (bytes != 0) Runtime.staticAlloc(bytes);
      return ret;  // Previous break location.
    }
  function _isdigit(chr) {
      return chr >= 48 && chr <= 57;
    }
  function _memset(ptr, value, num) {
      ptr = ptr|0; value = value|0; num = num|0;
      var stop = 0, value4 = 0, stop4 = 0, unaligned = 0;
      stop = (ptr + num)|0;
      if ((num|0) >= 20) {
        // This is unaligned, but quite large, so work hard to get to aligned settings
        value = value & 0xff;
        unaligned = ptr & 3;
        value4 = value | (value << 8) | (value << 16) | (value << 24);
        stop4 = stop & ~3;
        if (unaligned) {
          unaligned = (ptr + 4 - unaligned)|0;
          while ((ptr|0) < (unaligned|0)) { // no need to check for stop, since we have large num
            HEAP8[(ptr)]=value;
            ptr = (ptr+1)|0;
          }
        }
        while ((ptr|0) < (stop4|0)) {
          HEAP32[((ptr)>>2)]=value4;
          ptr = (ptr+4)|0;
        }
      }
      while ((ptr|0) < (stop|0)) {
        HEAP8[(ptr)]=value;
        ptr = (ptr+1)|0;
      }
    }
  function _free(){}
  var Browser={};
__ATINIT__.unshift({ func: function() { if (!Module["noFSInit"] && !FS.init.initialized) FS.init() } });__ATMAIN__.push({ func: function() { FS.ignorePermissions = false } });__ATEXIT__.push({ func: function() { FS.quit() } });Module["FS_createFolder"] = FS.createFolder;Module["FS_createPath"] = FS.createPath;Module["FS_createDataFile"] = FS.createDataFile;Module["FS_createPreloadedFile"] = FS.createPreloadedFile;Module["FS_createLazyFile"] = FS.createLazyFile;Module["FS_createLink"] = FS.createLink;Module["FS_createDevice"] = FS.createDevice;
___setErrNo(0);
Module["requestFullScreen"] = function(lockPointer, resizeCanvas) { Browser.requestFullScreen(lockPointer, resizeCanvas) };
  Module["requestAnimationFrame"] = function(func) { Browser.requestAnimationFrame(func) };
  Module["pauseMainLoop"] = function() { Browser.mainLoop.pause() };
  Module["resumeMainLoop"] = function() { Browser.mainLoop.resume() };
var FUNCTION_TABLE = [0, 0];
// EMSCRIPTEN_START_FUNCS
function _usage($name) {
  var label = 0;
  var __stackBase__  = STACKTOP; assert(!(STACKTOP&3)); assert((STACKTOP|0) < (STACK_MAX|0));
  var $1;
  $1=$name;
  var $2=$1;
  var $3=_printf(((5243504)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$2,tempInt));
  var $4=_printf(((5243472)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $5=_printf(((5243344)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $6=_printf(((5243300)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $7=_printf(((5243252)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $8=_printf(((5243180)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $9=_printf(((5243120)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  var $10=_printf(((5243072)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
  STACKTOP = __stackBase__;
  return;
}
function _allocate() {
  var label = 0;
  var $1=HEAP32[((5242952)>>2)];
  var $2=((($1)+(1))|0);
  HEAP32[((5242952)>>2)]=$2;
  var $3=_malloc(8);
  var $4=$3;
  return $4;
}
function _main($argc, $argv) {
  var label = 0;
  var __stackBase__  = STACKTOP; assert(!(STACKTOP&3)); assert((STACKTOP|0) < (STACK_MAX|0));
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $max_iterations;
      var $dirty;
      var $num_lists;
      var $tail;
      var $initial_length;
      var $growth_rate;
      var $c;
      var $i;
      var $j;
      var $k;
      var $accumulate;
      var $lists;
      var $growth;
      var $arg;
      var $e;
      var $trav;
      var $e1;
      var $trav2;
      $1=0;
      $2=$argc;
      $3=$argv;
      $max_iterations=1000;
      $dirty=1;
      $num_lists=196;
      $tail=1;
      $initial_length=1;
      $growth_rate=0.3330000042915344;
      $c=0;
      $i=0;
      $j=0;
      $k=0;
      $accumulate=0;
      $lists=0;
      $growth=0;
      $arg=1;
      var $4=_printf(((5243008)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 1)|0,STACKTOP = ((((STACKTOP)+3)>>2)<<2),assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=0,tempInt));
      label = 3; break;
    case 3: 
      var $6=$arg;
      var $7=$2;
      var $8=(($6)|(0)) < (($7)|(0));
      if ($8) { label = 4; break; } else { label = 17; break; }
    case 4: 
      var $10=$arg;
      var $11=$3;
      var $12=(($11+($10<<2))|0);
      var $13=HEAP32[(($12)>>2)];
      var $14=(($13)|0);
      var $15=HEAP8[($14)];
      var $16=(($15 << 24) >> 24);
      var $17=(($16)|(0))!=45;
      if ($17) { label = 6; break; } else { label = 5; break; }
    case 5: 
      var $19=$arg;
      var $20=$3;
      var $21=(($20+($19<<2))|0);
      var $22=HEAP32[(($21)>>2)];
      var $23=(($22+2)|0);
      var $24=HEAP8[($23)];
      var $25=(($24 << 24) >> 24);
      var $26=(($25)|(0))!=0;
      if ($26) { label = 6; break; } else { label = 7; break; }
    case 6: 
      var $28=$arg;
      var $29=$3;
      var $30=(($29+($28<<2))|0);
      var $31=HEAP32[(($30)>>2)];
      var $32=_printf(((5242988)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$31,tempInt));
      var $33=$3;
      var $34=(($33)|0);
      var $35=HEAP32[(($34)>>2)];
      _usage($35);
      $1=-1;
      label = 59; break;
    case 7: 
      var $37=$arg;
      var $38=$3;
      var $39=(($38+($37<<2))|0);
      var $40=HEAP32[(($39)>>2)];
      var $41=(($40+1)|0);
      var $42=HEAP8[($41)];
      $c=$42;
      var $43=$arg;
      var $44=((($43)+(1))|0);
      $arg=$44;
      var $45=$c;
      var $46=(($45 << 24) >> 24);
      if ((($46)|(0))==100) {
        label = 8; break;
      }
      else if ((($46)|(0))==103) {
        label = 9; break;
      }
      else if ((($46)|(0))==105) {
        label = 10; break;
      }
      else if ((($46)|(0))==108) {
        label = 11; break;
      }
      else if ((($46)|(0))==110) {
        label = 12; break;
      }
      else if ((($46)|(0))==115) {
        label = 13; break;
      }
      else if ((($46)|(0))==116) {
        label = 14; break;
      }
      else {
      label = 15; break;
      }
    case 8: 
      $dirty=1;
      label = 16; break;
    case 9: 
      var $49=$arg;
      var $50=((($49)+(1))|0);
      $arg=$50;
      var $51=$3;
      var $52=(($51+($49<<2))|0);
      var $53=HEAP32[(($52)>>2)];
      var $54=_atof($53);
      var $55=$54;
      $growth_rate=$55;
      label = 16; break;
    case 10: 
      var $57=$arg;
      var $58=((($57)+(1))|0);
      $arg=$58;
      var $59=$3;
      var $60=(($59+($57<<2))|0);
      var $61=HEAP32[(($60)>>2)];
      var $62=_atoi($61);
      $max_iterations=$62;
      label = 16; break;
    case 11: 
      var $64=$arg;
      var $65=((($64)+(1))|0);
      $arg=$65;
      var $66=$3;
      var $67=(($66+($64<<2))|0);
      var $68=HEAP32[(($67)>>2)];
      var $69=_atoi($68);
      $initial_length=$69;
      label = 16; break;
    case 12: 
      var $71=$arg;
      var $72=((($71)+(1))|0);
      $arg=$72;
      var $73=$3;
      var $74=(($73+($71<<2))|0);
      var $75=HEAP32[(($74)>>2)];
      var $76=_atoi($75);
      $num_lists=$76;
      label = 16; break;
    case 13: 
      var $78=$arg;
      var $79=((($78)+(1))|0);
      $arg=$79;
      var $80=$3;
      var $81=(($80+($78<<2))|0);
      var $82=HEAP32[(($81)>>2)];
      var $83=_atoi($82);
      HEAP32[((5242984)>>2)]=$83;
      label = 16; break;
    case 14: 
      $tail=1;
      label = 16; break;
    case 15: 
      var $86=$c;
      var $87=(($86 << 24) >> 24);
      var $88=_printf(((5243444)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$87,tempInt));
      var $89=$3;
      var $90=(($89)|0);
      var $91=HEAP32[(($90)>>2)];
      _usage($91);
      $1=-1;
      label = 59; break;
    case 16: 
      label = 3; break;
    case 17: 
      var $94=$num_lists;
      var $95=($94<<2);
      var $96=_malloc($95);
      var $97=$96;
      $lists=$97;
      $i=0;
      label = 18; break;
    case 18: 
      var $99=$i;
      var $100=$num_lists;
      var $101=(($99)|(0)) < (($100)|(0));
      if ($101) { label = 19; break; } else { label = 21; break; }
    case 19: 
      var $103=$i;
      var $104=$lists;
      var $105=(($104+($103<<2))|0);
      HEAP32[(($105)>>2)]=0;
      label = 20; break;
    case 20: 
      var $107=$i;
      var $108=((($107)+(1))|0);
      $i=$108;
      label = 18; break;
    case 21: 
      $i=0;
      label = 22; break;
    case 22: 
      var $111=$i;
      var $112=$initial_length;
      var $113=(($111)|(0)) < (($112)|(0));
      if ($113) { label = 23; break; } else { label = 29; break; }
    case 23: 
      $j=0;
      label = 24; break;
    case 24: 
      var $116=$j;
      var $117=$num_lists;
      var $118=(($116)|(0)) < (($117)|(0));
      if ($118) { label = 25; break; } else { label = 27; break; }
    case 25: 
      var $120=_allocate();
      $e=$120;
      var $121=$j;
      var $122=$lists;
      var $123=(($122+($121<<2))|0);
      var $124=HEAP32[(($123)>>2)];
      var $125=$e;
      var $126=(($125)|0);
      HEAP32[(($126)>>2)]=$124;
      var $127=$e;
      var $128=(($127+4)|0);
      HEAP32[(($128)>>2)]=0;
      var $129=$e;
      var $130=$j;
      var $131=$lists;
      var $132=(($131+($130<<2))|0);
      HEAP32[(($132)>>2)]=$129;
      label = 26; break;
    case 26: 
      var $134=$j;
      var $135=((($134)+(1))|0);
      $j=$135;
      label = 24; break;
    case 27: 
      label = 28; break;
    case 28: 
      var $138=$i;
      var $139=((($138)+(1))|0);
      $i=$139;
      label = 22; break;
    case 29: 
      $i=0;
      label = 30; break;
    case 30: 
      var $142=$i;
      var $143=$max_iterations;
      var $144=(($142)|(0)) < (($143)|(0));
      if ($144) { label = 31; break; } else { label = 58; break; }
    case 31: 
      var $146=$i;
      var $147=((($146)|(0)))%(1000);
      var $148=(($147)|(0))==0;
      if ($148) { label = 32; break; } else { label = 33; break; }
    case 32: 
      var $150=$i;
      var $151=_printf(((5243440)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$150,tempInt));
      label = 33; break;
    case 33: 
      $j=0;
      label = 34; break;
    case 34: 
      var $154=$j;
      var $155=$num_lists;
      var $156=(($154)|(0)) < (($155)|(0));
      if ($156) { label = 35; break; } else { label = 42; break; }
    case 35: 
      var $158=$j;
      var $159=$lists;
      var $160=(($159+($158<<2))|0);
      var $161=HEAP32[(($160)>>2)];
      $trav=$161;
      label = 36; break;
    case 36: 
      var $163=$trav;
      var $164=(($163)|(0))!=0;
      if ($164) { label = 37; break; } else { label = 40; break; }
    case 37: 
      var $166=$trav;
      var $167=(($166+4)|0);
      var $168=HEAP32[(($167)>>2)];
      var $169=$accumulate;
      var $170=((($169)+($168))|0);
      $accumulate=$170;
      var $171=$dirty;
      var $172=(($171)|(0))!=0;
      if ($172) { label = 38; break; } else { label = 39; break; }
    case 38: 
      var $174=$trav;
      var $175=(($174+4)|0);
      var $176=HEAP32[(($175)>>2)];
      var $177=((($176)+(1))|0);
      HEAP32[(($175)>>2)]=$177;
      label = 39; break;
    case 39: 
      var $179=$trav;
      var $180=(($179)|0);
      var $181=HEAP32[(($180)>>2)];
      $trav=$181;
      label = 36; break;
    case 40: 
      label = 41; break;
    case 41: 
      var $184=$j;
      var $185=((($184)+(1))|0);
      $j=$185;
      label = 34; break;
    case 42: 
      var $187=$growth_rate;
      var $188=$growth;
      var $189=($188)+($187);
      $growth=$189;
      var $190=$growth;
      var $191=(($190)&-1);
      $j=$191;
      var $192=$j;
      var $193=(($192)|(0));
      var $194=$growth;
      var $195=($194)-($193);
      $growth=$195;
      label = 43; break;
    case 43: 
      var $197=$j;
      var $198=(($197)|(0)) > 0;
      if ($198) { label = 44; break; } else { label = 56; break; }
    case 44: 
      $k=0;
      label = 45; break;
    case 45: 
      var $201=$k;
      var $202=$num_lists;
      var $203=(($201)|(0)) < (($202)|(0));
      if ($203) { label = 46; break; } else { label = 54; break; }
    case 46: 
      var $205=_allocate();
      $e1=$205;
      var $206=$k;
      var $207=$j;
      var $208=((($206)+($207))|0);
      var $209=$e1;
      var $210=(($209+4)|0);
      HEAP32[(($210)>>2)]=$208;
      var $211=$tail;
      var $212=(($211)|(0))!=0;
      if ($212) { label = 47; break; } else { label = 51; break; }
    case 47: 
      var $214=$k;
      var $215=$lists;
      var $216=(($215+($214<<2))|0);
      var $217=HEAP32[(($216)>>2)];
      $trav2=$217;
      label = 48; break;
    case 48: 
      var $219=$trav2;
      var $220=(($219)|0);
      var $221=HEAP32[(($220)>>2)];
      var $222=(($221)|(0))!=0;
      if ($222) { label = 49; break; } else { label = 50; break; }
    case 49: 
      var $224=$trav2;
      var $225=(($224)|0);
      var $226=HEAP32[(($225)>>2)];
      $trav2=$226;
      label = 48; break;
    case 50: 
      var $228=$e1;
      var $229=$trav2;
      var $230=(($229)|0);
      HEAP32[(($230)>>2)]=$228;
      var $231=$e1;
      var $232=(($231)|0);
      HEAP32[(($232)>>2)]=0;
      label = 52; break;
    case 51: 
      var $234=$k;
      var $235=$lists;
      var $236=(($235+($234<<2))|0);
      var $237=HEAP32[(($236)>>2)];
      var $238=$e1;
      var $239=(($238)|0);
      HEAP32[(($239)>>2)]=$237;
      var $240=$e1;
      var $241=$k;
      var $242=$lists;
      var $243=(($242+($241<<2))|0);
      HEAP32[(($243)>>2)]=$240;
      label = 52; break;
    case 52: 
      label = 53; break;
    case 53: 
      var $246=$k;
      var $247=((($246)+(1))|0);
      $k=$247;
      label = 45; break;
    case 54: 
      label = 55; break;
    case 55: 
      var $250=$j;
      var $251=((($250)-(1))|0);
      $j=$251;
      label = 43; break;
    case 56: 
      label = 57; break;
    case 57: 
      var $254=$i;
      var $255=((($254)+(1))|0);
      $i=$255;
      label = 30; break;
    case 58: 
      var $257=$accumulate;
      var $258=_printf(((5243424)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$257,tempInt));
      var $259=HEAP32[((5242952)>>2)];
      var $260=_printf(((5243404)|0), (tempInt=STACKTOP,STACKTOP = (STACKTOP + 4)|0,assert((STACKTOP|0) < (STACK_MAX|0)),HEAP32[((tempInt)>>2)]=$259,tempInt));
      $1=0;
      label = 59; break;
    case 59: 
      var $262=$1;
      STACKTOP = __stackBase__;
      return $262;
    default: assert(0, "bad label: " + label);
  }
}
Module["_main"] = _main;
function _malloc($bytes) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $mem;
      var $nb;
      var $idx;
      var $smallbits;
      var $b;
      var $p;
      var $F;
      var $b1;
      var $p2;
      var $r;
      var $rsize;
      var $i;
      var $leftbits;
      var $leastbit;
      var $Y;
      var $K;
      var $N;
      var $F3;
      var $DVS;
      var $DV;
      var $I;
      var $B;
      var $F4;
      var $rsize5;
      var $p6;
      var $r7;
      var $dvs;
      var $rsize8;
      var $p9;
      var $r10;
      $1=$bytes;
      var $2=$1;
      var $3=(($2)>>>(0)) <= 244;
      if ($3) { label = 3; break; } else { label = 42; break; }
    case 3: 
      var $5=$1;
      var $6=(($5)>>>(0)) < 11;
      if ($6) { label = 4; break; } else { label = 5; break; }
    case 4: 
      var $14 = 16;label = 6; break;
    case 5: 
      var $9=$1;
      var $10=((($9)+(4))|0);
      var $11=((($10)+(7))|0);
      var $12=$11 & -8;
      var $14 = $12;label = 6; break;
    case 6: 
      var $14;
      $nb=$14;
      var $15=$nb;
      var $16=$15 >>> 3;
      $idx=$16;
      var $17=HEAP32[((((5243512)|0))>>2)];
      var $18=$idx;
      var $19=$17 >>> (($18)>>>(0));
      $smallbits=$19;
      var $20=$smallbits;
      var $21=$20 & 3;
      var $22=(($21)|(0))!=0;
      if ($22) { label = 7; break; } else { label = 16; break; }
    case 7: 
      var $24=$smallbits;
      var $25=$24 ^ -1;
      var $26=$25 & 1;
      var $27=$idx;
      var $28=((($27)+($26))|0);
      $idx=$28;
      var $29=$idx;
      var $30=$29 << 1;
      var $31=((((5243552)|0)+($30<<2))|0);
      var $32=$31;
      var $33=$32;
      $b=$33;
      var $34=$b;
      var $35=(($34+8)|0);
      var $36=HEAP32[(($35)>>2)];
      $p=$36;
      var $37=$p;
      var $38=(($37+8)|0);
      var $39=HEAP32[(($38)>>2)];
      $F=$39;
      var $40=$b;
      var $41=$F;
      var $42=(($40)|(0))==(($41)|(0));
      if ($42) { label = 8; break; } else { label = 9; break; }
    case 8: 
      var $44=$idx;
      var $45=1 << $44;
      var $46=$45 ^ -1;
      var $47=HEAP32[((((5243512)|0))>>2)];
      var $48=$47 & $46;
      HEAP32[((((5243512)|0))>>2)]=$48;
      label = 15; break;
    case 9: 
      var $50=$F;
      var $51=$50;
      var $52=HEAP32[((((5243528)|0))>>2)];
      var $53=(($51)>>>(0)) >= (($52)>>>(0));
      if ($53) { label = 10; break; } else { var $61 = 0;label = 11; break; }
    case 10: 
      var $55=$F;
      var $56=(($55+12)|0);
      var $57=HEAP32[(($56)>>2)];
      var $58=$p;
      var $59=(($57)|(0))==(($58)|(0));
      var $61 = $59;label = 11; break;
    case 11: 
      var $61;
      var $62=(($61)&(1));
      var $63=($62);
      var $64=(($63)|(0))!=0;
      if ($64) { label = 12; break; } else { label = 13; break; }
    case 12: 
      var $66=$b;
      var $67=$F;
      var $68=(($67+12)|0);
      HEAP32[(($68)>>2)]=$66;
      var $69=$F;
      var $70=$b;
      var $71=(($70+8)|0);
      HEAP32[(($71)>>2)]=$69;
      label = 14; break;
    case 13: 
      _abort();
      throw "Reached an unreachable!"
    case 14: 
      label = 15; break;
    case 15: 
      var $75=$idx;
      var $76=$75 << 3;
      var $77=$76 | 1;
      var $78=$77 | 2;
      var $79=$p;
      var $80=(($79+4)|0);
      HEAP32[(($80)>>2)]=$78;
      var $81=$p;
      var $82=$81;
      var $83=$idx;
      var $84=$83 << 3;
      var $85=(($82+$84)|0);
      var $86=$85;
      var $87=(($86+4)|0);
      var $88=HEAP32[(($87)>>2)];
      var $89=$88 | 1;
      HEAP32[(($87)>>2)]=$89;
      var $90=$p;
      var $91=$90;
      var $92=(($91+8)|0);
      $mem=$92;
      label = 58; break;
    case 16: 
      var $94=$nb;
      var $95=HEAP32[((((5243520)|0))>>2)];
      var $96=(($94)>>>(0)) > (($95)>>>(0));
      if ($96) { label = 17; break; } else { label = 40; break; }
    case 17: 
      var $98=$smallbits;
      var $99=(($98)|(0))!=0;
      if ($99) { label = 18; break; } else { label = 35; break; }
    case 18: 
      var $101=$smallbits;
      var $102=$idx;
      var $103=$101 << $102;
      var $104=$idx;
      var $105=1 << $104;
      var $106=$105 << 1;
      var $107=$idx;
      var $108=1 << $107;
      var $109=$108 << 1;
      var $110=(((-$109))|0);
      var $111=$106 | $110;
      var $112=$103 & $111;
      $leftbits=$112;
      var $113=$leftbits;
      var $114=$leftbits;
      var $115=(((-$114))|0);
      var $116=$113 & $115;
      $leastbit=$116;
      var $117=$leastbit;
      var $118=((($117)-(1))|0);
      $Y=$118;
      var $119=$Y;
      var $120=$119 >>> 12;
      var $121=$120 & 16;
      $K=$121;
      var $122=$K;
      $N=$122;
      var $123=$K;
      var $124=$Y;
      var $125=$124 >>> (($123)>>>(0));
      $Y=$125;
      var $126=$Y;
      var $127=$126 >>> 5;
      var $128=$127 & 8;
      $K=$128;
      var $129=$N;
      var $130=((($129)+($128))|0);
      $N=$130;
      var $131=$K;
      var $132=$Y;
      var $133=$132 >>> (($131)>>>(0));
      $Y=$133;
      var $134=$Y;
      var $135=$134 >>> 2;
      var $136=$135 & 4;
      $K=$136;
      var $137=$N;
      var $138=((($137)+($136))|0);
      $N=$138;
      var $139=$K;
      var $140=$Y;
      var $141=$140 >>> (($139)>>>(0));
      $Y=$141;
      var $142=$Y;
      var $143=$142 >>> 1;
      var $144=$143 & 2;
      $K=$144;
      var $145=$N;
      var $146=((($145)+($144))|0);
      $N=$146;
      var $147=$K;
      var $148=$Y;
      var $149=$148 >>> (($147)>>>(0));
      $Y=$149;
      var $150=$Y;
      var $151=$150 >>> 1;
      var $152=$151 & 1;
      $K=$152;
      var $153=$N;
      var $154=((($153)+($152))|0);
      $N=$154;
      var $155=$K;
      var $156=$Y;
      var $157=$156 >>> (($155)>>>(0));
      $Y=$157;
      var $158=$N;
      var $159=$Y;
      var $160=((($158)+($159))|0);
      $i=$160;
      var $161=$i;
      var $162=$161 << 1;
      var $163=((((5243552)|0)+($162<<2))|0);
      var $164=$163;
      var $165=$164;
      $b1=$165;
      var $166=$b1;
      var $167=(($166+8)|0);
      var $168=HEAP32[(($167)>>2)];
      $p2=$168;
      var $169=$p2;
      var $170=(($169+8)|0);
      var $171=HEAP32[(($170)>>2)];
      $F3=$171;
      var $172=$b1;
      var $173=$F3;
      var $174=(($172)|(0))==(($173)|(0));
      if ($174) { label = 19; break; } else { label = 20; break; }
    case 19: 
      var $176=$i;
      var $177=1 << $176;
      var $178=$177 ^ -1;
      var $179=HEAP32[((((5243512)|0))>>2)];
      var $180=$179 & $178;
      HEAP32[((((5243512)|0))>>2)]=$180;
      label = 26; break;
    case 20: 
      var $182=$F3;
      var $183=$182;
      var $184=HEAP32[((((5243528)|0))>>2)];
      var $185=(($183)>>>(0)) >= (($184)>>>(0));
      if ($185) { label = 21; break; } else { var $193 = 0;label = 22; break; }
    case 21: 
      var $187=$F3;
      var $188=(($187+12)|0);
      var $189=HEAP32[(($188)>>2)];
      var $190=$p2;
      var $191=(($189)|(0))==(($190)|(0));
      var $193 = $191;label = 22; break;
    case 22: 
      var $193;
      var $194=(($193)&(1));
      var $195=($194);
      var $196=(($195)|(0))!=0;
      if ($196) { label = 23; break; } else { label = 24; break; }
    case 23: 
      var $198=$b1;
      var $199=$F3;
      var $200=(($199+12)|0);
      HEAP32[(($200)>>2)]=$198;
      var $201=$F3;
      var $202=$b1;
      var $203=(($202+8)|0);
      HEAP32[(($203)>>2)]=$201;
      label = 25; break;
    case 24: 
      _abort();
      throw "Reached an unreachable!"
    case 25: 
      label = 26; break;
    case 26: 
      var $207=$i;
      var $208=$207 << 3;
      var $209=$nb;
      var $210=((($208)-($209))|0);
      $rsize=$210;
      var $211=$nb;
      var $212=$211 | 1;
      var $213=$212 | 2;
      var $214=$p2;
      var $215=(($214+4)|0);
      HEAP32[(($215)>>2)]=$213;
      var $216=$p2;
      var $217=$216;
      var $218=$nb;
      var $219=(($217+$218)|0);
      var $220=$219;
      $r=$220;
      var $221=$rsize;
      var $222=$221 | 1;
      var $223=$r;
      var $224=(($223+4)|0);
      HEAP32[(($224)>>2)]=$222;
      var $225=$rsize;
      var $226=$r;
      var $227=$226;
      var $228=$rsize;
      var $229=(($227+$228)|0);
      var $230=$229;
      var $231=(($230)|0);
      HEAP32[(($231)>>2)]=$225;
      var $232=HEAP32[((((5243520)|0))>>2)];
      $DVS=$232;
      var $233=$DVS;
      var $234=(($233)|(0))!=0;
      if ($234) { label = 27; break; } else { label = 34; break; }
    case 27: 
      var $236=HEAP32[((((5243532)|0))>>2)];
      $DV=$236;
      var $237=$DVS;
      var $238=$237 >>> 3;
      $I=$238;
      var $239=$I;
      var $240=$239 << 1;
      var $241=((((5243552)|0)+($240<<2))|0);
      var $242=$241;
      var $243=$242;
      $B=$243;
      var $244=$B;
      $F4=$244;
      var $245=HEAP32[((((5243512)|0))>>2)];
      var $246=$I;
      var $247=1 << $246;
      var $248=$245 & $247;
      var $249=(($248)|(0))!=0;
      if ($249) { label = 29; break; } else { label = 28; break; }
    case 28: 
      var $251=$I;
      var $252=1 << $251;
      var $253=HEAP32[((((5243512)|0))>>2)];
      var $254=$253 | $252;
      HEAP32[((((5243512)|0))>>2)]=$254;
      label = 33; break;
    case 29: 
      var $256=$B;
      var $257=(($256+8)|0);
      var $258=HEAP32[(($257)>>2)];
      var $259=$258;
      var $260=HEAP32[((((5243528)|0))>>2)];
      var $261=(($259)>>>(0)) >= (($260)>>>(0));
      var $262=(($261)&(1));
      var $263=($262);
      var $264=(($263)|(0))!=0;
      if ($264) { label = 30; break; } else { label = 31; break; }
    case 30: 
      var $266=$B;
      var $267=(($266+8)|0);
      var $268=HEAP32[(($267)>>2)];
      $F4=$268;
      label = 32; break;
    case 31: 
      _abort();
      throw "Reached an unreachable!"
    case 32: 
      label = 33; break;
    case 33: 
      var $272=$DV;
      var $273=$B;
      var $274=(($273+8)|0);
      HEAP32[(($274)>>2)]=$272;
      var $275=$DV;
      var $276=$F4;
      var $277=(($276+12)|0);
      HEAP32[(($277)>>2)]=$275;
      var $278=$F4;
      var $279=$DV;
      var $280=(($279+8)|0);
      HEAP32[(($280)>>2)]=$278;
      var $281=$B;
      var $282=$DV;
      var $283=(($282+12)|0);
      HEAP32[(($283)>>2)]=$281;
      label = 34; break;
    case 34: 
      var $285=$rsize;
      HEAP32[((((5243520)|0))>>2)]=$285;
      var $286=$r;
      HEAP32[((((5243532)|0))>>2)]=$286;
      var $287=$p2;
      var $288=$287;
      var $289=(($288+8)|0);
      $mem=$289;
      label = 58; break;
    case 35: 
      var $291=HEAP32[((((5243516)|0))>>2)];
      var $292=(($291)|(0))!=0;
      if ($292) { label = 36; break; } else { label = 38; break; }
    case 36: 
      var $294=$nb;
      var $295=_tmalloc_small(5243512, $294);
      $mem=$295;
      var $296=(($295)|(0))!=0;
      if ($296) { label = 37; break; } else { label = 38; break; }
    case 37: 
      label = 58; break;
    case 38: 
      label = 39; break;
    case 39: 
      label = 40; break;
    case 40: 
      label = 41; break;
    case 41: 
      label = 49; break;
    case 42: 
      var $303=$1;
      var $304=(($303)>>>(0)) >= 4294967232;
      if ($304) { label = 43; break; } else { label = 44; break; }
    case 43: 
      $nb=-1;
      label = 48; break;
    case 44: 
      var $307=$1;
      var $308=((($307)+(4))|0);
      var $309=((($308)+(7))|0);
      var $310=$309 & -8;
      $nb=$310;
      var $311=HEAP32[((((5243516)|0))>>2)];
      var $312=(($311)|(0))!=0;
      if ($312) { label = 45; break; } else { label = 47; break; }
    case 45: 
      var $314=$nb;
      var $315=_tmalloc_large(5243512, $314);
      $mem=$315;
      var $316=(($315)|(0))!=0;
      if ($316) { label = 46; break; } else { label = 47; break; }
    case 46: 
      label = 58; break;
    case 47: 
      label = 48; break;
    case 48: 
      label = 49; break;
    case 49: 
      var $321=$nb;
      var $322=HEAP32[((((5243520)|0))>>2)];
      var $323=(($321)>>>(0)) <= (($322)>>>(0));
      if ($323) { label = 50; break; } else { label = 54; break; }
    case 50: 
      var $325=HEAP32[((((5243520)|0))>>2)];
      var $326=$nb;
      var $327=((($325)-($326))|0);
      $rsize5=$327;
      var $328=HEAP32[((((5243532)|0))>>2)];
      $p6=$328;
      var $329=$rsize5;
      var $330=(($329)>>>(0)) >= 16;
      if ($330) { label = 51; break; } else { label = 52; break; }
    case 51: 
      var $332=$p6;
      var $333=$332;
      var $334=$nb;
      var $335=(($333+$334)|0);
      var $336=$335;
      HEAP32[((((5243532)|0))>>2)]=$336;
      $r7=$336;
      var $337=$rsize5;
      HEAP32[((((5243520)|0))>>2)]=$337;
      var $338=$rsize5;
      var $339=$338 | 1;
      var $340=$r7;
      var $341=(($340+4)|0);
      HEAP32[(($341)>>2)]=$339;
      var $342=$rsize5;
      var $343=$r7;
      var $344=$343;
      var $345=$rsize5;
      var $346=(($344+$345)|0);
      var $347=$346;
      var $348=(($347)|0);
      HEAP32[(($348)>>2)]=$342;
      var $349=$nb;
      var $350=$349 | 1;
      var $351=$350 | 2;
      var $352=$p6;
      var $353=(($352+4)|0);
      HEAP32[(($353)>>2)]=$351;
      label = 53; break;
    case 52: 
      var $355=HEAP32[((((5243520)|0))>>2)];
      $dvs=$355;
      HEAP32[((((5243520)|0))>>2)]=0;
      HEAP32[((((5243532)|0))>>2)]=0;
      var $356=$dvs;
      var $357=$356 | 1;
      var $358=$357 | 2;
      var $359=$p6;
      var $360=(($359+4)|0);
      HEAP32[(($360)>>2)]=$358;
      var $361=$p6;
      var $362=$361;
      var $363=$dvs;
      var $364=(($362+$363)|0);
      var $365=$364;
      var $366=(($365+4)|0);
      var $367=HEAP32[(($366)>>2)];
      var $368=$367 | 1;
      HEAP32[(($366)>>2)]=$368;
      label = 53; break;
    case 53: 
      var $370=$p6;
      var $371=$370;
      var $372=(($371+8)|0);
      $mem=$372;
      label = 58; break;
    case 54: 
      var $374=$nb;
      var $375=HEAP32[((((5243524)|0))>>2)];
      var $376=(($374)>>>(0)) < (($375)>>>(0));
      if ($376) { label = 55; break; } else { label = 56; break; }
    case 55: 
      var $378=$nb;
      var $379=HEAP32[((((5243524)|0))>>2)];
      var $380=((($379)-($378))|0);
      HEAP32[((((5243524)|0))>>2)]=$380;
      $rsize8=$380;
      var $381=HEAP32[((((5243536)|0))>>2)];
      $p9=$381;
      var $382=$p9;
      var $383=$382;
      var $384=$nb;
      var $385=(($383+$384)|0);
      var $386=$385;
      HEAP32[((((5243536)|0))>>2)]=$386;
      $r10=$386;
      var $387=$rsize8;
      var $388=$387 | 1;
      var $389=$r10;
      var $390=(($389+4)|0);
      HEAP32[(($390)>>2)]=$388;
      var $391=$nb;
      var $392=$391 | 1;
      var $393=$392 | 2;
      var $394=$p9;
      var $395=(($394+4)|0);
      HEAP32[(($395)>>2)]=$393;
      var $396=$p9;
      var $397=$396;
      var $398=(($397+8)|0);
      $mem=$398;
      label = 58; break;
    case 56: 
      label = 57; break;
    case 57: 
      var $401=$nb;
      var $402=_sys_alloc(5243512, $401);
      $mem=$402;
      label = 58; break;
    case 58: 
      var $404=$mem;
      return $404;
    default: assert(0, "bad label: " + label);
  }
}
function _tmalloc_small($m, $nb) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $t;
      var $v;
      var $rsize;
      var $i;
      var $leastbit;
      var $Y;
      var $K;
      var $N;
      var $trem;
      var $r;
      var $XP;
      var $R;
      var $F;
      var $RP;
      var $CP;
      var $H;
      var $C0;
      var $C1;
      var $DVS;
      var $DV;
      var $I;
      var $B;
      var $F1;
      $1=$m;
      $2=$nb;
      var $3=$1;
      var $4=(($3+4)|0);
      var $5=HEAP32[(($4)>>2)];
      var $6=$1;
      var $7=(($6+4)|0);
      var $8=HEAP32[(($7)>>2)];
      var $9=(((-$8))|0);
      var $10=$5 & $9;
      $leastbit=$10;
      var $11=$leastbit;
      var $12=((($11)-(1))|0);
      $Y=$12;
      var $13=$Y;
      var $14=$13 >>> 12;
      var $15=$14 & 16;
      $K=$15;
      var $16=$K;
      $N=$16;
      var $17=$K;
      var $18=$Y;
      var $19=$18 >>> (($17)>>>(0));
      $Y=$19;
      var $20=$Y;
      var $21=$20 >>> 5;
      var $22=$21 & 8;
      $K=$22;
      var $23=$N;
      var $24=((($23)+($22))|0);
      $N=$24;
      var $25=$K;
      var $26=$Y;
      var $27=$26 >>> (($25)>>>(0));
      $Y=$27;
      var $28=$Y;
      var $29=$28 >>> 2;
      var $30=$29 & 4;
      $K=$30;
      var $31=$N;
      var $32=((($31)+($30))|0);
      $N=$32;
      var $33=$K;
      var $34=$Y;
      var $35=$34 >>> (($33)>>>(0));
      $Y=$35;
      var $36=$Y;
      var $37=$36 >>> 1;
      var $38=$37 & 2;
      $K=$38;
      var $39=$N;
      var $40=((($39)+($38))|0);
      $N=$40;
      var $41=$K;
      var $42=$Y;
      var $43=$42 >>> (($41)>>>(0));
      $Y=$43;
      var $44=$Y;
      var $45=$44 >>> 1;
      var $46=$45 & 1;
      $K=$46;
      var $47=$N;
      var $48=((($47)+($46))|0);
      $N=$48;
      var $49=$K;
      var $50=$Y;
      var $51=$50 >>> (($49)>>>(0));
      $Y=$51;
      var $52=$N;
      var $53=$Y;
      var $54=((($52)+($53))|0);
      $i=$54;
      var $55=$i;
      var $56=$1;
      var $57=(($56+304)|0);
      var $58=(($57+($55<<2))|0);
      var $59=HEAP32[(($58)>>2)];
      $t=$59;
      $v=$59;
      var $60=$t;
      var $61=(($60+4)|0);
      var $62=HEAP32[(($61)>>2)];
      var $63=$62 & -8;
      var $64=$2;
      var $65=((($63)-($64))|0);
      $rsize=$65;
      label = 3; break;
    case 3: 
      var $67=$t;
      var $68=(($67+16)|0);
      var $69=(($68)|0);
      var $70=HEAP32[(($69)>>2)];
      var $71=(($70)|(0))!=0;
      if ($71) { label = 4; break; } else { label = 5; break; }
    case 4: 
      var $73=$t;
      var $74=(($73+16)|0);
      var $75=(($74)|0);
      var $76=HEAP32[(($75)>>2)];
      var $83 = $76;label = 6; break;
    case 5: 
      var $78=$t;
      var $79=(($78+16)|0);
      var $80=(($79+4)|0);
      var $81=HEAP32[(($80)>>2)];
      var $83 = $81;label = 6; break;
    case 6: 
      var $83;
      $t=$83;
      var $84=(($83)|(0))!=0;
      if ($84) { label = 7; break; } else { label = 10; break; }
    case 7: 
      var $86=$t;
      var $87=(($86+4)|0);
      var $88=HEAP32[(($87)>>2)];
      var $89=$88 & -8;
      var $90=$2;
      var $91=((($89)-($90))|0);
      $trem=$91;
      var $92=$trem;
      var $93=$rsize;
      var $94=(($92)>>>(0)) < (($93)>>>(0));
      if ($94) { label = 8; break; } else { label = 9; break; }
    case 8: 
      var $96=$trem;
      $rsize=$96;
      var $97=$t;
      $v=$97;
      label = 9; break;
    case 9: 
      label = 3; break;
    case 10: 
      var $100=$v;
      var $101=$100;
      var $102=$1;
      var $103=(($102+16)|0);
      var $104=HEAP32[(($103)>>2)];
      var $105=(($101)>>>(0)) >= (($104)>>>(0));
      var $106=(($105)&(1));
      var $107=($106);
      var $108=(($107)|(0))!=0;
      if ($108) { label = 11; break; } else { label = 73; break; }
    case 11: 
      var $110=$v;
      var $111=$110;
      var $112=$2;
      var $113=(($111+$112)|0);
      var $114=$113;
      $r=$114;
      var $115=$v;
      var $116=$115;
      var $117=$r;
      var $118=$117;
      var $119=(($116)>>>(0)) < (($118)>>>(0));
      var $120=(($119)&(1));
      var $121=($120);
      var $122=(($121)|(0))!=0;
      if ($122) { label = 12; break; } else { label = 72; break; }
    case 12: 
      var $124=$v;
      var $125=(($124+24)|0);
      var $126=HEAP32[(($125)>>2)];
      $XP=$126;
      var $127=$v;
      var $128=(($127+12)|0);
      var $129=HEAP32[(($128)>>2)];
      var $130=$v;
      var $131=(($129)|(0))!=(($130)|(0));
      if ($131) { label = 13; break; } else { label = 20; break; }
    case 13: 
      var $133=$v;
      var $134=(($133+8)|0);
      var $135=HEAP32[(($134)>>2)];
      $F=$135;
      var $136=$v;
      var $137=(($136+12)|0);
      var $138=HEAP32[(($137)>>2)];
      $R=$138;
      var $139=$F;
      var $140=$139;
      var $141=$1;
      var $142=(($141+16)|0);
      var $143=HEAP32[(($142)>>2)];
      var $144=(($140)>>>(0)) >= (($143)>>>(0));
      if ($144) { label = 14; break; } else { var $158 = 0;label = 16; break; }
    case 14: 
      var $146=$F;
      var $147=(($146+12)|0);
      var $148=HEAP32[(($147)>>2)];
      var $149=$v;
      var $150=(($148)|(0))==(($149)|(0));
      if ($150) { label = 15; break; } else { var $158 = 0;label = 16; break; }
    case 15: 
      var $152=$R;
      var $153=(($152+8)|0);
      var $154=HEAP32[(($153)>>2)];
      var $155=$v;
      var $156=(($154)|(0))==(($155)|(0));
      var $158 = $156;label = 16; break;
    case 16: 
      var $158;
      var $159=(($158)&(1));
      var $160=($159);
      var $161=(($160)|(0))!=0;
      if ($161) { label = 17; break; } else { label = 18; break; }
    case 17: 
      var $163=$R;
      var $164=$F;
      var $165=(($164+12)|0);
      HEAP32[(($165)>>2)]=$163;
      var $166=$F;
      var $167=$R;
      var $168=(($167+8)|0);
      HEAP32[(($168)>>2)]=$166;
      label = 19; break;
    case 18: 
      _abort();
      throw "Reached an unreachable!"
    case 19: 
      label = 32; break;
    case 20: 
      var $172=$v;
      var $173=(($172+16)|0);
      var $174=(($173+4)|0);
      $RP=$174;
      var $175=HEAP32[(($174)>>2)];
      $R=$175;
      var $176=(($175)|(0))!=0;
      if ($176) { label = 22; break; } else { label = 21; break; }
    case 21: 
      var $178=$v;
      var $179=(($178+16)|0);
      var $180=(($179)|0);
      $RP=$180;
      var $181=HEAP32[(($180)>>2)];
      $R=$181;
      var $182=(($181)|(0))!=0;
      if ($182) { label = 22; break; } else { label = 31; break; }
    case 22: 
      label = 23; break;
    case 23: 
      var $185=$R;
      var $186=(($185+16)|0);
      var $187=(($186+4)|0);
      $CP=$187;
      var $188=HEAP32[(($187)>>2)];
      var $189=(($188)|(0))!=0;
      if ($189) { var $197 = 1;label = 25; break; } else { label = 24; break; }
    case 24: 
      var $191=$R;
      var $192=(($191+16)|0);
      var $193=(($192)|0);
      $CP=$193;
      var $194=HEAP32[(($193)>>2)];
      var $195=(($194)|(0))!=0;
      var $197 = $195;label = 25; break;
    case 25: 
      var $197;
      if ($197) { label = 26; break; } else { label = 27; break; }
    case 26: 
      var $199=$CP;
      $RP=$199;
      var $200=HEAP32[(($199)>>2)];
      $R=$200;
      label = 23; break;
    case 27: 
      var $202=$RP;
      var $203=$202;
      var $204=$1;
      var $205=(($204+16)|0);
      var $206=HEAP32[(($205)>>2)];
      var $207=(($203)>>>(0)) >= (($206)>>>(0));
      var $208=(($207)&(1));
      var $209=($208);
      var $210=(($209)|(0))!=0;
      if ($210) { label = 28; break; } else { label = 29; break; }
    case 28: 
      var $212=$RP;
      HEAP32[(($212)>>2)]=0;
      label = 30; break;
    case 29: 
      _abort();
      throw "Reached an unreachable!"
    case 30: 
      label = 31; break;
    case 31: 
      label = 32; break;
    case 32: 
      var $217=$XP;
      var $218=(($217)|(0))!=0;
      if ($218) { label = 33; break; } else { label = 60; break; }
    case 33: 
      var $220=$v;
      var $221=(($220+28)|0);
      var $222=HEAP32[(($221)>>2)];
      var $223=$1;
      var $224=(($223+304)|0);
      var $225=(($224+($222<<2))|0);
      $H=$225;
      var $226=$v;
      var $227=$H;
      var $228=HEAP32[(($227)>>2)];
      var $229=(($226)|(0))==(($228)|(0));
      if ($229) { label = 34; break; } else { label = 37; break; }
    case 34: 
      var $231=$R;
      var $232=$H;
      HEAP32[(($232)>>2)]=$231;
      var $233=(($231)|(0))==0;
      if ($233) { label = 35; break; } else { label = 36; break; }
    case 35: 
      var $235=$v;
      var $236=(($235+28)|0);
      var $237=HEAP32[(($236)>>2)];
      var $238=1 << $237;
      var $239=$238 ^ -1;
      var $240=$1;
      var $241=(($240+4)|0);
      var $242=HEAP32[(($241)>>2)];
      var $243=$242 & $239;
      HEAP32[(($241)>>2)]=$243;
      label = 36; break;
    case 36: 
      label = 44; break;
    case 37: 
      var $246=$XP;
      var $247=$246;
      var $248=$1;
      var $249=(($248+16)|0);
      var $250=HEAP32[(($249)>>2)];
      var $251=(($247)>>>(0)) >= (($250)>>>(0));
      var $252=(($251)&(1));
      var $253=($252);
      var $254=(($253)|(0))!=0;
      if ($254) { label = 38; break; } else { label = 42; break; }
    case 38: 
      var $256=$XP;
      var $257=(($256+16)|0);
      var $258=(($257)|0);
      var $259=HEAP32[(($258)>>2)];
      var $260=$v;
      var $261=(($259)|(0))==(($260)|(0));
      if ($261) { label = 39; break; } else { label = 40; break; }
    case 39: 
      var $263=$R;
      var $264=$XP;
      var $265=(($264+16)|0);
      var $266=(($265)|0);
      HEAP32[(($266)>>2)]=$263;
      label = 41; break;
    case 40: 
      var $268=$R;
      var $269=$XP;
      var $270=(($269+16)|0);
      var $271=(($270+4)|0);
      HEAP32[(($271)>>2)]=$268;
      label = 41; break;
    case 41: 
      label = 43; break;
    case 42: 
      _abort();
      throw "Reached an unreachable!"
    case 43: 
      label = 44; break;
    case 44: 
      var $276=$R;
      var $277=(($276)|(0))!=0;
      if ($277) { label = 45; break; } else { label = 59; break; }
    case 45: 
      var $279=$R;
      var $280=$279;
      var $281=$1;
      var $282=(($281+16)|0);
      var $283=HEAP32[(($282)>>2)];
      var $284=(($280)>>>(0)) >= (($283)>>>(0));
      var $285=(($284)&(1));
      var $286=($285);
      var $287=(($286)|(0))!=0;
      if ($287) { label = 46; break; } else { label = 57; break; }
    case 46: 
      var $289=$XP;
      var $290=$R;
      var $291=(($290+24)|0);
      HEAP32[(($291)>>2)]=$289;
      var $292=$v;
      var $293=(($292+16)|0);
      var $294=(($293)|0);
      var $295=HEAP32[(($294)>>2)];
      $C0=$295;
      var $296=(($295)|(0))!=0;
      if ($296) { label = 47; break; } else { label = 51; break; }
    case 47: 
      var $298=$C0;
      var $299=$298;
      var $300=$1;
      var $301=(($300+16)|0);
      var $302=HEAP32[(($301)>>2)];
      var $303=(($299)>>>(0)) >= (($302)>>>(0));
      var $304=(($303)&(1));
      var $305=($304);
      var $306=(($305)|(0))!=0;
      if ($306) { label = 48; break; } else { label = 49; break; }
    case 48: 
      var $308=$C0;
      var $309=$R;
      var $310=(($309+16)|0);
      var $311=(($310)|0);
      HEAP32[(($311)>>2)]=$308;
      var $312=$R;
      var $313=$C0;
      var $314=(($313+24)|0);
      HEAP32[(($314)>>2)]=$312;
      label = 50; break;
    case 49: 
      _abort();
      throw "Reached an unreachable!"
    case 50: 
      label = 51; break;
    case 51: 
      var $318=$v;
      var $319=(($318+16)|0);
      var $320=(($319+4)|0);
      var $321=HEAP32[(($320)>>2)];
      $C1=$321;
      var $322=(($321)|(0))!=0;
      if ($322) { label = 52; break; } else { label = 56; break; }
    case 52: 
      var $324=$C1;
      var $325=$324;
      var $326=$1;
      var $327=(($326+16)|0);
      var $328=HEAP32[(($327)>>2)];
      var $329=(($325)>>>(0)) >= (($328)>>>(0));
      var $330=(($329)&(1));
      var $331=($330);
      var $332=(($331)|(0))!=0;
      if ($332) { label = 53; break; } else { label = 54; break; }
    case 53: 
      var $334=$C1;
      var $335=$R;
      var $336=(($335+16)|0);
      var $337=(($336+4)|0);
      HEAP32[(($337)>>2)]=$334;
      var $338=$R;
      var $339=$C1;
      var $340=(($339+24)|0);
      HEAP32[(($340)>>2)]=$338;
      label = 55; break;
    case 54: 
      _abort();
      throw "Reached an unreachable!"
    case 55: 
      label = 56; break;
    case 56: 
      label = 58; break;
    case 57: 
      _abort();
      throw "Reached an unreachable!"
    case 58: 
      label = 59; break;
    case 59: 
      label = 60; break;
    case 60: 
      var $348=$rsize;
      var $349=(($348)>>>(0)) < 16;
      if ($349) { label = 61; break; } else { label = 62; break; }
    case 61: 
      var $351=$rsize;
      var $352=$2;
      var $353=((($351)+($352))|0);
      var $354=$353 | 1;
      var $355=$354 | 2;
      var $356=$v;
      var $357=(($356+4)|0);
      HEAP32[(($357)>>2)]=$355;
      var $358=$v;
      var $359=$358;
      var $360=$rsize;
      var $361=$2;
      var $362=((($360)+($361))|0);
      var $363=(($359+$362)|0);
      var $364=$363;
      var $365=(($364+4)|0);
      var $366=HEAP32[(($365)>>2)];
      var $367=$366 | 1;
      HEAP32[(($365)>>2)]=$367;
      label = 71; break;
    case 62: 
      var $369=$2;
      var $370=$369 | 1;
      var $371=$370 | 2;
      var $372=$v;
      var $373=(($372+4)|0);
      HEAP32[(($373)>>2)]=$371;
      var $374=$rsize;
      var $375=$374 | 1;
      var $376=$r;
      var $377=(($376+4)|0);
      HEAP32[(($377)>>2)]=$375;
      var $378=$rsize;
      var $379=$r;
      var $380=$379;
      var $381=$rsize;
      var $382=(($380+$381)|0);
      var $383=$382;
      var $384=(($383)|0);
      HEAP32[(($384)>>2)]=$378;
      var $385=$1;
      var $386=(($385+8)|0);
      var $387=HEAP32[(($386)>>2)];
      $DVS=$387;
      var $388=$DVS;
      var $389=(($388)|(0))!=0;
      if ($389) { label = 63; break; } else { label = 70; break; }
    case 63: 
      var $391=$1;
      var $392=(($391+20)|0);
      var $393=HEAP32[(($392)>>2)];
      $DV=$393;
      var $394=$DVS;
      var $395=$394 >>> 3;
      $I=$395;
      var $396=$I;
      var $397=$396 << 1;
      var $398=$1;
      var $399=(($398+40)|0);
      var $400=(($399+($397<<2))|0);
      var $401=$400;
      var $402=$401;
      $B=$402;
      var $403=$B;
      $F1=$403;
      var $404=$1;
      var $405=(($404)|0);
      var $406=HEAP32[(($405)>>2)];
      var $407=$I;
      var $408=1 << $407;
      var $409=$406 & $408;
      var $410=(($409)|(0))!=0;
      if ($410) { label = 65; break; } else { label = 64; break; }
    case 64: 
      var $412=$I;
      var $413=1 << $412;
      var $414=$1;
      var $415=(($414)|0);
      var $416=HEAP32[(($415)>>2)];
      var $417=$416 | $413;
      HEAP32[(($415)>>2)]=$417;
      label = 69; break;
    case 65: 
      var $419=$B;
      var $420=(($419+8)|0);
      var $421=HEAP32[(($420)>>2)];
      var $422=$421;
      var $423=$1;
      var $424=(($423+16)|0);
      var $425=HEAP32[(($424)>>2)];
      var $426=(($422)>>>(0)) >= (($425)>>>(0));
      var $427=(($426)&(1));
      var $428=($427);
      var $429=(($428)|(0))!=0;
      if ($429) { label = 66; break; } else { label = 67; break; }
    case 66: 
      var $431=$B;
      var $432=(($431+8)|0);
      var $433=HEAP32[(($432)>>2)];
      $F1=$433;
      label = 68; break;
    case 67: 
      _abort();
      throw "Reached an unreachable!"
    case 68: 
      label = 69; break;
    case 69: 
      var $437=$DV;
      var $438=$B;
      var $439=(($438+8)|0);
      HEAP32[(($439)>>2)]=$437;
      var $440=$DV;
      var $441=$F1;
      var $442=(($441+12)|0);
      HEAP32[(($442)>>2)]=$440;
      var $443=$F1;
      var $444=$DV;
      var $445=(($444+8)|0);
      HEAP32[(($445)>>2)]=$443;
      var $446=$B;
      var $447=$DV;
      var $448=(($447+12)|0);
      HEAP32[(($448)>>2)]=$446;
      label = 70; break;
    case 70: 
      var $450=$rsize;
      var $451=$1;
      var $452=(($451+8)|0);
      HEAP32[(($452)>>2)]=$450;
      var $453=$r;
      var $454=$1;
      var $455=(($454+20)|0);
      HEAP32[(($455)>>2)]=$453;
      label = 71; break;
    case 71: 
      var $457=$v;
      var $458=$457;
      var $459=(($458+8)|0);
      return $459;
    case 72: 
      label = 73; break;
    case 73: 
      _abort();
      throw "Reached an unreachable!"
    default: assert(0, "bad label: " + label);
  }
}
function _tmalloc_large($m, $nb) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $v;
      var $rsize;
      var $t;
      var $idx;
      var $X;
      var $Y;
      var $N;
      var $K;
      var $sizebits;
      var $rst;
      var $rt;
      var $trem;
      var $leftbits;
      var $i;
      var $leastbit;
      var $Y1;
      var $K2;
      var $N3;
      var $trem4;
      var $r;
      var $XP;
      var $R;
      var $F;
      var $RP;
      var $CP;
      var $H;
      var $C0;
      var $C1;
      var $I;
      var $B;
      var $F5;
      var $TP;
      var $H6;
      var $I7;
      var $X8;
      var $Y9;
      var $N10;
      var $K11;
      var $T;
      var $K12;
      var $C;
      var $F13;
      $2=$m;
      $3=$nb;
      $v=0;
      var $4=$3;
      var $5=(((-$4))|0);
      $rsize=$5;
      var $6=$3;
      var $7=$6 >>> 8;
      $X=$7;
      var $8=$X;
      var $9=(($8)|(0))==0;
      if ($9) { label = 3; break; } else { label = 4; break; }
    case 3: 
      $idx=0;
      label = 8; break;
    case 4: 
      var $12=$X;
      var $13=(($12)>>>(0)) > 65535;
      if ($13) { label = 5; break; } else { label = 6; break; }
    case 5: 
      $idx=31;
      label = 7; break;
    case 6: 
      var $16=$X;
      $Y=$16;
      var $17=$Y;
      var $18=((($17)-(256))|0);
      var $19=$18 >>> 16;
      var $20=$19 & 8;
      $N=$20;
      var $21=$N;
      var $22=$Y;
      var $23=$22 << $21;
      $Y=$23;
      var $24=((($23)-(4096))|0);
      var $25=$24 >>> 16;
      var $26=$25 & 4;
      $K=$26;
      var $27=$K;
      var $28=$N;
      var $29=((($28)+($27))|0);
      $N=$29;
      var $30=$K;
      var $31=$Y;
      var $32=$31 << $30;
      $Y=$32;
      var $33=((($32)-(16384))|0);
      var $34=$33 >>> 16;
      var $35=$34 & 2;
      $K=$35;
      var $36=$N;
      var $37=((($36)+($35))|0);
      $N=$37;
      var $38=$N;
      var $39=(((14)-($38))|0);
      var $40=$K;
      var $41=$Y;
      var $42=$41 << $40;
      $Y=$42;
      var $43=$42 >>> 15;
      var $44=((($39)+($43))|0);
      $K=$44;
      var $45=$K;
      var $46=$45 << 1;
      var $47=$3;
      var $48=$K;
      var $49=((($48)+(7))|0);
      var $50=$47 >>> (($49)>>>(0));
      var $51=$50 & 1;
      var $52=((($46)+($51))|0);
      $idx=$52;
      label = 7; break;
    case 7: 
      label = 8; break;
    case 8: 
      var $55=$idx;
      var $56=$2;
      var $57=(($56+304)|0);
      var $58=(($57+($55<<2))|0);
      var $59=HEAP32[(($58)>>2)];
      $t=$59;
      var $60=(($59)|(0))!=0;
      if ($60) { label = 9; break; } else { label = 24; break; }
    case 9: 
      var $62=$3;
      var $63=$idx;
      var $64=(($63)|(0))==31;
      if ($64) { label = 10; break; } else { label = 11; break; }
    case 10: 
      var $73 = 0;label = 12; break;
    case 11: 
      var $67=$idx;
      var $68=$67 >>> 1;
      var $69=((($68)+(8))|0);
      var $70=((($69)-(2))|0);
      var $71=(((31)-($70))|0);
      var $73 = $71;label = 12; break;
    case 12: 
      var $73;
      var $74=$62 << $73;
      $sizebits=$74;
      $rst=0;
      label = 13; break;
    case 13: 
      var $76=$t;
      var $77=(($76+4)|0);
      var $78=HEAP32[(($77)>>2)];
      var $79=$78 & -8;
      var $80=$3;
      var $81=((($79)-($80))|0);
      $trem=$81;
      var $82=$trem;
      var $83=$rsize;
      var $84=(($82)>>>(0)) < (($83)>>>(0));
      if ($84) { label = 14; break; } else { label = 17; break; }
    case 14: 
      var $86=$t;
      $v=$86;
      var $87=$trem;
      $rsize=$87;
      var $88=(($87)|(0))==0;
      if ($88) { label = 15; break; } else { label = 16; break; }
    case 15: 
      label = 23; break;
    case 16: 
      label = 17; break;
    case 17: 
      var $92=$t;
      var $93=(($92+16)|0);
      var $94=(($93+4)|0);
      var $95=HEAP32[(($94)>>2)];
      $rt=$95;
      var $96=$sizebits;
      var $97=$96 >>> 31;
      var $98=$97 & 1;
      var $99=$t;
      var $100=(($99+16)|0);
      var $101=(($100+($98<<2))|0);
      var $102=HEAP32[(($101)>>2)];
      $t=$102;
      var $103=$rt;
      var $104=(($103)|(0))!=0;
      if ($104) { label = 18; break; } else { label = 20; break; }
    case 18: 
      var $106=$rt;
      var $107=$t;
      var $108=(($106)|(0))!=(($107)|(0));
      if ($108) { label = 19; break; } else { label = 20; break; }
    case 19: 
      var $110=$rt;
      $rst=$110;
      label = 20; break;
    case 20: 
      var $112=$t;
      var $113=(($112)|(0))==0;
      if ($113) { label = 21; break; } else { label = 22; break; }
    case 21: 
      var $115=$rst;
      $t=$115;
      label = 23; break;
    case 22: 
      var $117=$sizebits;
      var $118=$117 << 1;
      $sizebits=$118;
      label = 13; break;
    case 23: 
      label = 24; break;
    case 24: 
      var $121=$t;
      var $122=(($121)|(0))==0;
      if ($122) { label = 25; break; } else { label = 29; break; }
    case 25: 
      var $124=$v;
      var $125=(($124)|(0))==0;
      if ($125) { label = 26; break; } else { label = 29; break; }
    case 26: 
      var $127=$idx;
      var $128=1 << $127;
      var $129=$128 << 1;
      var $130=$idx;
      var $131=1 << $130;
      var $132=$131 << 1;
      var $133=(((-$132))|0);
      var $134=$129 | $133;
      var $135=$2;
      var $136=(($135+4)|0);
      var $137=HEAP32[(($136)>>2)];
      var $138=$134 & $137;
      $leftbits=$138;
      var $139=$leftbits;
      var $140=(($139)|(0))!=0;
      if ($140) { label = 27; break; } else { label = 28; break; }
    case 27: 
      var $142=$leftbits;
      var $143=$leftbits;
      var $144=(((-$143))|0);
      var $145=$142 & $144;
      $leastbit=$145;
      var $146=$leastbit;
      var $147=((($146)-(1))|0);
      $Y1=$147;
      var $148=$Y1;
      var $149=$148 >>> 12;
      var $150=$149 & 16;
      $K2=$150;
      var $151=$K2;
      $N3=$151;
      var $152=$K2;
      var $153=$Y1;
      var $154=$153 >>> (($152)>>>(0));
      $Y1=$154;
      var $155=$Y1;
      var $156=$155 >>> 5;
      var $157=$156 & 8;
      $K2=$157;
      var $158=$N3;
      var $159=((($158)+($157))|0);
      $N3=$159;
      var $160=$K2;
      var $161=$Y1;
      var $162=$161 >>> (($160)>>>(0));
      $Y1=$162;
      var $163=$Y1;
      var $164=$163 >>> 2;
      var $165=$164 & 4;
      $K2=$165;
      var $166=$N3;
      var $167=((($166)+($165))|0);
      $N3=$167;
      var $168=$K2;
      var $169=$Y1;
      var $170=$169 >>> (($168)>>>(0));
      $Y1=$170;
      var $171=$Y1;
      var $172=$171 >>> 1;
      var $173=$172 & 2;
      $K2=$173;
      var $174=$N3;
      var $175=((($174)+($173))|0);
      $N3=$175;
      var $176=$K2;
      var $177=$Y1;
      var $178=$177 >>> (($176)>>>(0));
      $Y1=$178;
      var $179=$Y1;
      var $180=$179 >>> 1;
      var $181=$180 & 1;
      $K2=$181;
      var $182=$N3;
      var $183=((($182)+($181))|0);
      $N3=$183;
      var $184=$K2;
      var $185=$Y1;
      var $186=$185 >>> (($184)>>>(0));
      $Y1=$186;
      var $187=$N3;
      var $188=$Y1;
      var $189=((($187)+($188))|0);
      $i=$189;
      var $190=$i;
      var $191=$2;
      var $192=(($191+304)|0);
      var $193=(($192+($190<<2))|0);
      var $194=HEAP32[(($193)>>2)];
      $t=$194;
      label = 28; break;
    case 28: 
      label = 29; break;
    case 29: 
      label = 30; break;
    case 30: 
      var $198=$t;
      var $199=(($198)|(0))!=0;
      if ($199) { label = 31; break; } else { label = 37; break; }
    case 31: 
      var $201=$t;
      var $202=(($201+4)|0);
      var $203=HEAP32[(($202)>>2)];
      var $204=$203 & -8;
      var $205=$3;
      var $206=((($204)-($205))|0);
      $trem4=$206;
      var $207=$trem4;
      var $208=$rsize;
      var $209=(($207)>>>(0)) < (($208)>>>(0));
      if ($209) { label = 32; break; } else { label = 33; break; }
    case 32: 
      var $211=$trem4;
      $rsize=$211;
      var $212=$t;
      $v=$212;
      label = 33; break;
    case 33: 
      var $214=$t;
      var $215=(($214+16)|0);
      var $216=(($215)|0);
      var $217=HEAP32[(($216)>>2)];
      var $218=(($217)|(0))!=0;
      if ($218) { label = 34; break; } else { label = 35; break; }
    case 34: 
      var $220=$t;
      var $221=(($220+16)|0);
      var $222=(($221)|0);
      var $223=HEAP32[(($222)>>2)];
      var $230 = $223;label = 36; break;
    case 35: 
      var $225=$t;
      var $226=(($225+16)|0);
      var $227=(($226+4)|0);
      var $228=HEAP32[(($227)>>2)];
      var $230 = $228;label = 36; break;
    case 36: 
      var $230;
      $t=$230;
      label = 30; break;
    case 37: 
      var $232=$v;
      var $233=(($232)|(0))!=0;
      if ($233) { label = 38; break; } else { label = 130; break; }
    case 38: 
      var $235=$rsize;
      var $236=$2;
      var $237=(($236+8)|0);
      var $238=HEAP32[(($237)>>2)];
      var $239=$3;
      var $240=((($238)-($239))|0);
      var $241=(($235)>>>(0)) < (($240)>>>(0));
      if ($241) { label = 39; break; } else { label = 130; break; }
    case 39: 
      var $243=$v;
      var $244=$243;
      var $245=$2;
      var $246=(($245+16)|0);
      var $247=HEAP32[(($246)>>2)];
      var $248=(($244)>>>(0)) >= (($247)>>>(0));
      var $249=(($248)&(1));
      var $250=($249);
      var $251=(($250)|(0))!=0;
      if ($251) { label = 40; break; } else { label = 129; break; }
    case 40: 
      var $253=$v;
      var $254=$253;
      var $255=$3;
      var $256=(($254+$255)|0);
      var $257=$256;
      $r=$257;
      var $258=$v;
      var $259=$258;
      var $260=$r;
      var $261=$260;
      var $262=(($259)>>>(0)) < (($261)>>>(0));
      var $263=(($262)&(1));
      var $264=($263);
      var $265=(($264)|(0))!=0;
      if ($265) { label = 41; break; } else { label = 128; break; }
    case 41: 
      var $267=$v;
      var $268=(($267+24)|0);
      var $269=HEAP32[(($268)>>2)];
      $XP=$269;
      var $270=$v;
      var $271=(($270+12)|0);
      var $272=HEAP32[(($271)>>2)];
      var $273=$v;
      var $274=(($272)|(0))!=(($273)|(0));
      if ($274) { label = 42; break; } else { label = 49; break; }
    case 42: 
      var $276=$v;
      var $277=(($276+8)|0);
      var $278=HEAP32[(($277)>>2)];
      $F=$278;
      var $279=$v;
      var $280=(($279+12)|0);
      var $281=HEAP32[(($280)>>2)];
      $R=$281;
      var $282=$F;
      var $283=$282;
      var $284=$2;
      var $285=(($284+16)|0);
      var $286=HEAP32[(($285)>>2)];
      var $287=(($283)>>>(0)) >= (($286)>>>(0));
      if ($287) { label = 43; break; } else { var $301 = 0;label = 45; break; }
    case 43: 
      var $289=$F;
      var $290=(($289+12)|0);
      var $291=HEAP32[(($290)>>2)];
      var $292=$v;
      var $293=(($291)|(0))==(($292)|(0));
      if ($293) { label = 44; break; } else { var $301 = 0;label = 45; break; }
    case 44: 
      var $295=$R;
      var $296=(($295+8)|0);
      var $297=HEAP32[(($296)>>2)];
      var $298=$v;
      var $299=(($297)|(0))==(($298)|(0));
      var $301 = $299;label = 45; break;
    case 45: 
      var $301;
      var $302=(($301)&(1));
      var $303=($302);
      var $304=(($303)|(0))!=0;
      if ($304) { label = 46; break; } else { label = 47; break; }
    case 46: 
      var $306=$R;
      var $307=$F;
      var $308=(($307+12)|0);
      HEAP32[(($308)>>2)]=$306;
      var $309=$F;
      var $310=$R;
      var $311=(($310+8)|0);
      HEAP32[(($311)>>2)]=$309;
      label = 48; break;
    case 47: 
      _abort();
      throw "Reached an unreachable!"
    case 48: 
      label = 61; break;
    case 49: 
      var $315=$v;
      var $316=(($315+16)|0);
      var $317=(($316+4)|0);
      $RP=$317;
      var $318=HEAP32[(($317)>>2)];
      $R=$318;
      var $319=(($318)|(0))!=0;
      if ($319) { label = 51; break; } else { label = 50; break; }
    case 50: 
      var $321=$v;
      var $322=(($321+16)|0);
      var $323=(($322)|0);
      $RP=$323;
      var $324=HEAP32[(($323)>>2)];
      $R=$324;
      var $325=(($324)|(0))!=0;
      if ($325) { label = 51; break; } else { label = 60; break; }
    case 51: 
      label = 52; break;
    case 52: 
      var $328=$R;
      var $329=(($328+16)|0);
      var $330=(($329+4)|0);
      $CP=$330;
      var $331=HEAP32[(($330)>>2)];
      var $332=(($331)|(0))!=0;
      if ($332) { var $340 = 1;label = 54; break; } else { label = 53; break; }
    case 53: 
      var $334=$R;
      var $335=(($334+16)|0);
      var $336=(($335)|0);
      $CP=$336;
      var $337=HEAP32[(($336)>>2)];
      var $338=(($337)|(0))!=0;
      var $340 = $338;label = 54; break;
    case 54: 
      var $340;
      if ($340) { label = 55; break; } else { label = 56; break; }
    case 55: 
      var $342=$CP;
      $RP=$342;
      var $343=HEAP32[(($342)>>2)];
      $R=$343;
      label = 52; break;
    case 56: 
      var $345=$RP;
      var $346=$345;
      var $347=$2;
      var $348=(($347+16)|0);
      var $349=HEAP32[(($348)>>2)];
      var $350=(($346)>>>(0)) >= (($349)>>>(0));
      var $351=(($350)&(1));
      var $352=($351);
      var $353=(($352)|(0))!=0;
      if ($353) { label = 57; break; } else { label = 58; break; }
    case 57: 
      var $355=$RP;
      HEAP32[(($355)>>2)]=0;
      label = 59; break;
    case 58: 
      _abort();
      throw "Reached an unreachable!"
    case 59: 
      label = 60; break;
    case 60: 
      label = 61; break;
    case 61: 
      var $360=$XP;
      var $361=(($360)|(0))!=0;
      if ($361) { label = 62; break; } else { label = 89; break; }
    case 62: 
      var $363=$v;
      var $364=(($363+28)|0);
      var $365=HEAP32[(($364)>>2)];
      var $366=$2;
      var $367=(($366+304)|0);
      var $368=(($367+($365<<2))|0);
      $H=$368;
      var $369=$v;
      var $370=$H;
      var $371=HEAP32[(($370)>>2)];
      var $372=(($369)|(0))==(($371)|(0));
      if ($372) { label = 63; break; } else { label = 66; break; }
    case 63: 
      var $374=$R;
      var $375=$H;
      HEAP32[(($375)>>2)]=$374;
      var $376=(($374)|(0))==0;
      if ($376) { label = 64; break; } else { label = 65; break; }
    case 64: 
      var $378=$v;
      var $379=(($378+28)|0);
      var $380=HEAP32[(($379)>>2)];
      var $381=1 << $380;
      var $382=$381 ^ -1;
      var $383=$2;
      var $384=(($383+4)|0);
      var $385=HEAP32[(($384)>>2)];
      var $386=$385 & $382;
      HEAP32[(($384)>>2)]=$386;
      label = 65; break;
    case 65: 
      label = 73; break;
    case 66: 
      var $389=$XP;
      var $390=$389;
      var $391=$2;
      var $392=(($391+16)|0);
      var $393=HEAP32[(($392)>>2)];
      var $394=(($390)>>>(0)) >= (($393)>>>(0));
      var $395=(($394)&(1));
      var $396=($395);
      var $397=(($396)|(0))!=0;
      if ($397) { label = 67; break; } else { label = 71; break; }
    case 67: 
      var $399=$XP;
      var $400=(($399+16)|0);
      var $401=(($400)|0);
      var $402=HEAP32[(($401)>>2)];
      var $403=$v;
      var $404=(($402)|(0))==(($403)|(0));
      if ($404) { label = 68; break; } else { label = 69; break; }
    case 68: 
      var $406=$R;
      var $407=$XP;
      var $408=(($407+16)|0);
      var $409=(($408)|0);
      HEAP32[(($409)>>2)]=$406;
      label = 70; break;
    case 69: 
      var $411=$R;
      var $412=$XP;
      var $413=(($412+16)|0);
      var $414=(($413+4)|0);
      HEAP32[(($414)>>2)]=$411;
      label = 70; break;
    case 70: 
      label = 72; break;
    case 71: 
      _abort();
      throw "Reached an unreachable!"
    case 72: 
      label = 73; break;
    case 73: 
      var $419=$R;
      var $420=(($419)|(0))!=0;
      if ($420) { label = 74; break; } else { label = 88; break; }
    case 74: 
      var $422=$R;
      var $423=$422;
      var $424=$2;
      var $425=(($424+16)|0);
      var $426=HEAP32[(($425)>>2)];
      var $427=(($423)>>>(0)) >= (($426)>>>(0));
      var $428=(($427)&(1));
      var $429=($428);
      var $430=(($429)|(0))!=0;
      if ($430) { label = 75; break; } else { label = 86; break; }
    case 75: 
      var $432=$XP;
      var $433=$R;
      var $434=(($433+24)|0);
      HEAP32[(($434)>>2)]=$432;
      var $435=$v;
      var $436=(($435+16)|0);
      var $437=(($436)|0);
      var $438=HEAP32[(($437)>>2)];
      $C0=$438;
      var $439=(($438)|(0))!=0;
      if ($439) { label = 76; break; } else { label = 80; break; }
    case 76: 
      var $441=$C0;
      var $442=$441;
      var $443=$2;
      var $444=(($443+16)|0);
      var $445=HEAP32[(($444)>>2)];
      var $446=(($442)>>>(0)) >= (($445)>>>(0));
      var $447=(($446)&(1));
      var $448=($447);
      var $449=(($448)|(0))!=0;
      if ($449) { label = 77; break; } else { label = 78; break; }
    case 77: 
      var $451=$C0;
      var $452=$R;
      var $453=(($452+16)|0);
      var $454=(($453)|0);
      HEAP32[(($454)>>2)]=$451;
      var $455=$R;
      var $456=$C0;
      var $457=(($456+24)|0);
      HEAP32[(($457)>>2)]=$455;
      label = 79; break;
    case 78: 
      _abort();
      throw "Reached an unreachable!"
    case 79: 
      label = 80; break;
    case 80: 
      var $461=$v;
      var $462=(($461+16)|0);
      var $463=(($462+4)|0);
      var $464=HEAP32[(($463)>>2)];
      $C1=$464;
      var $465=(($464)|(0))!=0;
      if ($465) { label = 81; break; } else { label = 85; break; }
    case 81: 
      var $467=$C1;
      var $468=$467;
      var $469=$2;
      var $470=(($469+16)|0);
      var $471=HEAP32[(($470)>>2)];
      var $472=(($468)>>>(0)) >= (($471)>>>(0));
      var $473=(($472)&(1));
      var $474=($473);
      var $475=(($474)|(0))!=0;
      if ($475) { label = 82; break; } else { label = 83; break; }
    case 82: 
      var $477=$C1;
      var $478=$R;
      var $479=(($478+16)|0);
      var $480=(($479+4)|0);
      HEAP32[(($480)>>2)]=$477;
      var $481=$R;
      var $482=$C1;
      var $483=(($482+24)|0);
      HEAP32[(($483)>>2)]=$481;
      label = 84; break;
    case 83: 
      _abort();
      throw "Reached an unreachable!"
    case 84: 
      label = 85; break;
    case 85: 
      label = 87; break;
    case 86: 
      _abort();
      throw "Reached an unreachable!"
    case 87: 
      label = 88; break;
    case 88: 
      label = 89; break;
    case 89: 
      var $491=$rsize;
      var $492=(($491)>>>(0)) < 16;
      if ($492) { label = 90; break; } else { label = 91; break; }
    case 90: 
      var $494=$rsize;
      var $495=$3;
      var $496=((($494)+($495))|0);
      var $497=$496 | 1;
      var $498=$497 | 2;
      var $499=$v;
      var $500=(($499+4)|0);
      HEAP32[(($500)>>2)]=$498;
      var $501=$v;
      var $502=$501;
      var $503=$rsize;
      var $504=$3;
      var $505=((($503)+($504))|0);
      var $506=(($502+$505)|0);
      var $507=$506;
      var $508=(($507+4)|0);
      var $509=HEAP32[(($508)>>2)];
      var $510=$509 | 1;
      HEAP32[(($508)>>2)]=$510;
      label = 127; break;
    case 91: 
      var $512=$3;
      var $513=$512 | 1;
      var $514=$513 | 2;
      var $515=$v;
      var $516=(($515+4)|0);
      HEAP32[(($516)>>2)]=$514;
      var $517=$rsize;
      var $518=$517 | 1;
      var $519=$r;
      var $520=(($519+4)|0);
      HEAP32[(($520)>>2)]=$518;
      var $521=$rsize;
      var $522=$r;
      var $523=$522;
      var $524=$rsize;
      var $525=(($523+$524)|0);
      var $526=$525;
      var $527=(($526)|0);
      HEAP32[(($527)>>2)]=$521;
      var $528=$rsize;
      var $529=$528 >>> 3;
      var $530=(($529)>>>(0)) < 32;
      if ($530) { label = 92; break; } else { label = 99; break; }
    case 92: 
      var $532=$rsize;
      var $533=$532 >>> 3;
      $I=$533;
      var $534=$I;
      var $535=$534 << 1;
      var $536=$2;
      var $537=(($536+40)|0);
      var $538=(($537+($535<<2))|0);
      var $539=$538;
      var $540=$539;
      $B=$540;
      var $541=$B;
      $F5=$541;
      var $542=$2;
      var $543=(($542)|0);
      var $544=HEAP32[(($543)>>2)];
      var $545=$I;
      var $546=1 << $545;
      var $547=$544 & $546;
      var $548=(($547)|(0))!=0;
      if ($548) { label = 94; break; } else { label = 93; break; }
    case 93: 
      var $550=$I;
      var $551=1 << $550;
      var $552=$2;
      var $553=(($552)|0);
      var $554=HEAP32[(($553)>>2)];
      var $555=$554 | $551;
      HEAP32[(($553)>>2)]=$555;
      label = 98; break;
    case 94: 
      var $557=$B;
      var $558=(($557+8)|0);
      var $559=HEAP32[(($558)>>2)];
      var $560=$559;
      var $561=$2;
      var $562=(($561+16)|0);
      var $563=HEAP32[(($562)>>2)];
      var $564=(($560)>>>(0)) >= (($563)>>>(0));
      var $565=(($564)&(1));
      var $566=($565);
      var $567=(($566)|(0))!=0;
      if ($567) { label = 95; break; } else { label = 96; break; }
    case 95: 
      var $569=$B;
      var $570=(($569+8)|0);
      var $571=HEAP32[(($570)>>2)];
      $F5=$571;
      label = 97; break;
    case 96: 
      _abort();
      throw "Reached an unreachable!"
    case 97: 
      label = 98; break;
    case 98: 
      var $575=$r;
      var $576=$B;
      var $577=(($576+8)|0);
      HEAP32[(($577)>>2)]=$575;
      var $578=$r;
      var $579=$F5;
      var $580=(($579+12)|0);
      HEAP32[(($580)>>2)]=$578;
      var $581=$F5;
      var $582=$r;
      var $583=(($582+8)|0);
      HEAP32[(($583)>>2)]=$581;
      var $584=$B;
      var $585=$r;
      var $586=(($585+12)|0);
      HEAP32[(($586)>>2)]=$584;
      label = 126; break;
    case 99: 
      var $588=$r;
      var $589=$588;
      $TP=$589;
      var $590=$rsize;
      var $591=$590 >>> 8;
      $X8=$591;
      var $592=$X8;
      var $593=(($592)|(0))==0;
      if ($593) { label = 100; break; } else { label = 101; break; }
    case 100: 
      $I7=0;
      label = 105; break;
    case 101: 
      var $596=$X8;
      var $597=(($596)>>>(0)) > 65535;
      if ($597) { label = 102; break; } else { label = 103; break; }
    case 102: 
      $I7=31;
      label = 104; break;
    case 103: 
      var $600=$X8;
      $Y9=$600;
      var $601=$Y9;
      var $602=((($601)-(256))|0);
      var $603=$602 >>> 16;
      var $604=$603 & 8;
      $N10=$604;
      var $605=$N10;
      var $606=$Y9;
      var $607=$606 << $605;
      $Y9=$607;
      var $608=((($607)-(4096))|0);
      var $609=$608 >>> 16;
      var $610=$609 & 4;
      $K11=$610;
      var $611=$K11;
      var $612=$N10;
      var $613=((($612)+($611))|0);
      $N10=$613;
      var $614=$K11;
      var $615=$Y9;
      var $616=$615 << $614;
      $Y9=$616;
      var $617=((($616)-(16384))|0);
      var $618=$617 >>> 16;
      var $619=$618 & 2;
      $K11=$619;
      var $620=$N10;
      var $621=((($620)+($619))|0);
      $N10=$621;
      var $622=$N10;
      var $623=(((14)-($622))|0);
      var $624=$K11;
      var $625=$Y9;
      var $626=$625 << $624;
      $Y9=$626;
      var $627=$626 >>> 15;
      var $628=((($623)+($627))|0);
      $K11=$628;
      var $629=$K11;
      var $630=$629 << 1;
      var $631=$rsize;
      var $632=$K11;
      var $633=((($632)+(7))|0);
      var $634=$631 >>> (($633)>>>(0));
      var $635=$634 & 1;
      var $636=((($630)+($635))|0);
      $I7=$636;
      label = 104; break;
    case 104: 
      label = 105; break;
    case 105: 
      var $639=$I7;
      var $640=$2;
      var $641=(($640+304)|0);
      var $642=(($641+($639<<2))|0);
      $H6=$642;
      var $643=$I7;
      var $644=$TP;
      var $645=(($644+28)|0);
      HEAP32[(($645)>>2)]=$643;
      var $646=$TP;
      var $647=(($646+16)|0);
      var $648=(($647+4)|0);
      HEAP32[(($648)>>2)]=0;
      var $649=$TP;
      var $650=(($649+16)|0);
      var $651=(($650)|0);
      HEAP32[(($651)>>2)]=0;
      var $652=$2;
      var $653=(($652+4)|0);
      var $654=HEAP32[(($653)>>2)];
      var $655=$I7;
      var $656=1 << $655;
      var $657=$654 & $656;
      var $658=(($657)|(0))!=0;
      if ($658) { label = 107; break; } else { label = 106; break; }
    case 106: 
      var $660=$I7;
      var $661=1 << $660;
      var $662=$2;
      var $663=(($662+4)|0);
      var $664=HEAP32[(($663)>>2)];
      var $665=$664 | $661;
      HEAP32[(($663)>>2)]=$665;
      var $666=$TP;
      var $667=$H6;
      HEAP32[(($667)>>2)]=$666;
      var $668=$H6;
      var $669=$668;
      var $670=$TP;
      var $671=(($670+24)|0);
      HEAP32[(($671)>>2)]=$669;
      var $672=$TP;
      var $673=$TP;
      var $674=(($673+12)|0);
      HEAP32[(($674)>>2)]=$672;
      var $675=$TP;
      var $676=(($675+8)|0);
      HEAP32[(($676)>>2)]=$672;
      label = 125; break;
    case 107: 
      var $678=$H6;
      var $679=HEAP32[(($678)>>2)];
      $T=$679;
      var $680=$rsize;
      var $681=$I7;
      var $682=(($681)|(0))==31;
      if ($682) { label = 108; break; } else { label = 109; break; }
    case 108: 
      var $691 = 0;label = 110; break;
    case 109: 
      var $685=$I7;
      var $686=$685 >>> 1;
      var $687=((($686)+(8))|0);
      var $688=((($687)-(2))|0);
      var $689=(((31)-($688))|0);
      var $691 = $689;label = 110; break;
    case 110: 
      var $691;
      var $692=$680 << $691;
      $K12=$692;
      label = 111; break;
    case 111: 
      var $694=$T;
      var $695=(($694+4)|0);
      var $696=HEAP32[(($695)>>2)];
      var $697=$696 & -8;
      var $698=$rsize;
      var $699=(($697)|(0))!=(($698)|(0));
      if ($699) { label = 112; break; } else { label = 118; break; }
    case 112: 
      var $701=$K12;
      var $702=$701 >>> 31;
      var $703=$702 & 1;
      var $704=$T;
      var $705=(($704+16)|0);
      var $706=(($705+($703<<2))|0);
      $C=$706;
      var $707=$K12;
      var $708=$707 << 1;
      $K12=$708;
      var $709=$C;
      var $710=HEAP32[(($709)>>2)];
      var $711=(($710)|(0))!=0;
      if ($711) { label = 113; break; } else { label = 114; break; }
    case 113: 
      var $713=$C;
      var $714=HEAP32[(($713)>>2)];
      $T=$714;
      label = 117; break;
    case 114: 
      var $716=$C;
      var $717=$716;
      var $718=$2;
      var $719=(($718+16)|0);
      var $720=HEAP32[(($719)>>2)];
      var $721=(($717)>>>(0)) >= (($720)>>>(0));
      var $722=(($721)&(1));
      var $723=($722);
      var $724=(($723)|(0))!=0;
      if ($724) { label = 115; break; } else { label = 116; break; }
    case 115: 
      var $726=$TP;
      var $727=$C;
      HEAP32[(($727)>>2)]=$726;
      var $728=$T;
      var $729=$TP;
      var $730=(($729+24)|0);
      HEAP32[(($730)>>2)]=$728;
      var $731=$TP;
      var $732=$TP;
      var $733=(($732+12)|0);
      HEAP32[(($733)>>2)]=$731;
      var $734=$TP;
      var $735=(($734+8)|0);
      HEAP32[(($735)>>2)]=$731;
      label = 124; break;
    case 116: 
      _abort();
      throw "Reached an unreachable!"
    case 117: 
      label = 123; break;
    case 118: 
      var $739=$T;
      var $740=(($739+8)|0);
      var $741=HEAP32[(($740)>>2)];
      $F13=$741;
      var $742=$T;
      var $743=$742;
      var $744=$2;
      var $745=(($744+16)|0);
      var $746=HEAP32[(($745)>>2)];
      var $747=(($743)>>>(0)) >= (($746)>>>(0));
      if ($747) { label = 119; break; } else { var $756 = 0;label = 120; break; }
    case 119: 
      var $749=$F13;
      var $750=$749;
      var $751=$2;
      var $752=(($751+16)|0);
      var $753=HEAP32[(($752)>>2)];
      var $754=(($750)>>>(0)) >= (($753)>>>(0));
      var $756 = $754;label = 120; break;
    case 120: 
      var $756;
      var $757=(($756)&(1));
      var $758=($757);
      var $759=(($758)|(0))!=0;
      if ($759) { label = 121; break; } else { label = 122; break; }
    case 121: 
      var $761=$TP;
      var $762=$F13;
      var $763=(($762+12)|0);
      HEAP32[(($763)>>2)]=$761;
      var $764=$T;
      var $765=(($764+8)|0);
      HEAP32[(($765)>>2)]=$761;
      var $766=$F13;
      var $767=$TP;
      var $768=(($767+8)|0);
      HEAP32[(($768)>>2)]=$766;
      var $769=$T;
      var $770=$TP;
      var $771=(($770+12)|0);
      HEAP32[(($771)>>2)]=$769;
      var $772=$TP;
      var $773=(($772+24)|0);
      HEAP32[(($773)>>2)]=0;
      label = 124; break;
    case 122: 
      _abort();
      throw "Reached an unreachable!"
    case 123: 
      label = 111; break;
    case 124: 
      label = 125; break;
    case 125: 
      label = 126; break;
    case 126: 
      label = 127; break;
    case 127: 
      var $780=$v;
      var $781=$780;
      var $782=(($781+8)|0);
      $1=$782;
      label = 131; break;
    case 128: 
      label = 129; break;
    case 129: 
      _abort();
      throw "Reached an unreachable!"
    case 130: 
      $1=0;
      label = 131; break;
    case 131: 
      var $787=$1;
      return $787;
    default: assert(0, "bad label: " + label);
  }
}
function _segment_holding($m, $addr) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $sp;
      $2=$m;
      $3=$addr;
      var $4=$2;
      var $5=(($4+448)|0);
      $sp=$5;
      label = 3; break;
    case 3: 
      var $7=$3;
      var $8=$sp;
      var $9=(($8)|0);
      var $10=HEAP32[(($9)>>2)];
      var $11=(($7)>>>(0)) >= (($10)>>>(0));
      if ($11) { label = 4; break; } else { label = 6; break; }
    case 4: 
      var $13=$3;
      var $14=$sp;
      var $15=(($14)|0);
      var $16=HEAP32[(($15)>>2)];
      var $17=$sp;
      var $18=(($17+4)|0);
      var $19=HEAP32[(($18)>>2)];
      var $20=(($16+$19)|0);
      var $21=(($13)>>>(0)) < (($20)>>>(0));
      if ($21) { label = 5; break; } else { label = 6; break; }
    case 5: 
      var $23=$sp;
      $1=$23;
      label = 9; break;
    case 6: 
      var $25=$sp;
      var $26=(($25+8)|0);
      var $27=HEAP32[(($26)>>2)];
      $sp=$27;
      var $28=(($27)|(0))==0;
      if ($28) { label = 7; break; } else { label = 8; break; }
    case 7: 
      $1=0;
      label = 9; break;
    case 8: 
      label = 3; break;
    case 9: 
      var $32=$1;
      return $32;
    default: assert(0, "bad label: " + label);
  }
}
function _sys_alloc($m, $nb) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $tbase;
      var $tsize;
      var $mmap_flag;
      var $asize;
      var $mem;
      var $fp;
      var $br;
      var $ssize;
      var $ss;
      var $base;
      var $fp1;
      var $esize;
      var $end;
      var $br2;
      var $end3;
      var $ssize4;
      var $mn;
      var $sp;
      var $oldbase;
      var $rsize;
      var $p;
      var $r;
      $2=$m;
      $3=$nb;
      $tbase=-1;
      $tsize=0;
      $mmap_flag=0;
      var $4=HEAP32[((((5242956)|0))>>2)];
      var $5=(($4)|(0))!=0;
      if ($5) { var $10 = 1;label = 4; break; } else { label = 3; break; }
    case 3: 
      var $7=_init_mparams();
      var $8=(($7)|(0))!=0;
      var $10 = $8;label = 4; break;
    case 4: 
      var $10;
      var $11=(($10)&(1));
      var $12=$2;
      var $13=(($12+444)|0);
      var $14=HEAP32[(($13)>>2)];
      var $15=$14 & 0;
      var $16=(($15)|(0))!=0;
      if ($16) { label = 5; break; } else { label = 10; break; }
    case 5: 
      var $18=$3;
      var $19=HEAP32[((((5242968)|0))>>2)];
      var $20=(($18)>>>(0)) >= (($19)>>>(0));
      if ($20) { label = 6; break; } else { label = 10; break; }
    case 6: 
      var $22=$2;
      var $23=(($22+12)|0);
      var $24=HEAP32[(($23)>>2)];
      var $25=(($24)|(0))!=0;
      if ($25) { label = 7; break; } else { label = 10; break; }
    case 7: 
      var $27=$2;
      var $28=$3;
      var $29=_mmap_alloc($27, $28);
      $mem=$29;
      var $30=$mem;
      var $31=(($30)|(0))!=0;
      if ($31) { label = 8; break; } else { label = 9; break; }
    case 8: 
      var $33=$mem;
      $1=$33;
      label = 104; break;
    case 9: 
      label = 10; break;
    case 10: 
      var $36=$3;
      var $37=((($36)+(48))|0);
      var $38=HEAP32[((((5242964)|0))>>2)];
      var $39=((($38)-(1))|0);
      var $40=((($37)+($39))|0);
      var $41=HEAP32[((((5242964)|0))>>2)];
      var $42=((($41)-(1))|0);
      var $43=$42 ^ -1;
      var $44=$40 & $43;
      $asize=$44;
      var $45=$asize;
      var $46=$3;
      var $47=(($45)>>>(0)) <= (($46)>>>(0));
      if ($47) { label = 11; break; } else { label = 12; break; }
    case 11: 
      $1=0;
      label = 104; break;
    case 12: 
      var $50=$2;
      var $51=(($50+440)|0);
      var $52=HEAP32[(($51)>>2)];
      var $53=(($52)|(0))!=0;
      if ($53) { label = 13; break; } else { label = 17; break; }
    case 13: 
      var $55=$2;
      var $56=(($55+432)|0);
      var $57=HEAP32[(($56)>>2)];
      var $58=$asize;
      var $59=((($57)+($58))|0);
      $fp=$59;
      var $60=$fp;
      var $61=$2;
      var $62=(($61+432)|0);
      var $63=HEAP32[(($62)>>2)];
      var $64=(($60)>>>(0)) <= (($63)>>>(0));
      if ($64) { label = 15; break; } else { label = 14; break; }
    case 14: 
      var $66=$fp;
      var $67=$2;
      var $68=(($67+440)|0);
      var $69=HEAP32[(($68)>>2)];
      var $70=(($66)>>>(0)) > (($69)>>>(0));
      if ($70) { label = 15; break; } else { label = 16; break; }
    case 15: 
      $1=0;
      label = 104; break;
    case 16: 
      label = 17; break;
    case 17: 
      var $74=$2;
      var $75=(($74+444)|0);
      var $76=HEAP32[(($75)>>2)];
      var $77=$76 & 4;
      var $78=(($77)|(0))!=0;
      if ($78) { label = 54; break; } else { label = 18; break; }
    case 18: 
      $br=-1;
      var $80=$asize;
      $ssize=$80;
      var $81=$2;
      var $82=(($81+24)|0);
      var $83=HEAP32[(($82)>>2)];
      var $84=(($83)|(0))==0;
      if ($84) { label = 19; break; } else { label = 20; break; }
    case 19: 
      var $94 = 0;label = 21; break;
    case 20: 
      var $87=$2;
      var $88=$2;
      var $89=(($88+24)|0);
      var $90=HEAP32[(($89)>>2)];
      var $91=$90;
      var $92=_segment_holding($87, $91);
      var $94 = $92;label = 21; break;
    case 21: 
      var $94;
      $ss=$94;
      var $95=$ss;
      var $96=(($95)|(0))==0;
      if ($96) { label = 22; break; } else { label = 34; break; }
    case 22: 
      var $98=_sbrk(0);
      $base=$98;
      var $99=$base;
      var $100=(($99)|(0))!=-1;
      if ($100) { label = 23; break; } else { label = 33; break; }
    case 23: 
      var $102=$base;
      var $103=$102;
      var $104=HEAP32[((((5242960)|0))>>2)];
      var $105=((($104)-(1))|0);
      var $106=$103 & $105;
      var $107=(($106)|(0))==0;
      if ($107) { label = 25; break; } else { label = 24; break; }
    case 24: 
      var $109=$base;
      var $110=$109;
      var $111=HEAP32[((((5242960)|0))>>2)];
      var $112=((($111)-(1))|0);
      var $113=((($110)+($112))|0);
      var $114=HEAP32[((((5242960)|0))>>2)];
      var $115=((($114)-(1))|0);
      var $116=$115 ^ -1;
      var $117=$113 & $116;
      var $118=$base;
      var $119=$118;
      var $120=((($117)-($119))|0);
      var $121=$ssize;
      var $122=((($121)+($120))|0);
      $ssize=$122;
      label = 25; break;
    case 25: 
      var $124=$2;
      var $125=(($124+432)|0);
      var $126=HEAP32[(($125)>>2)];
      var $127=$ssize;
      var $128=((($126)+($127))|0);
      $fp1=$128;
      var $129=$ssize;
      var $130=$3;
      var $131=(($129)>>>(0)) > (($130)>>>(0));
      if ($131) { label = 26; break; } else { label = 32; break; }
    case 26: 
      var $133=$ssize;
      var $134=(($133)>>>(0)) < 2147483647;
      if ($134) { label = 27; break; } else { label = 32; break; }
    case 27: 
      var $136=$2;
      var $137=(($136+440)|0);
      var $138=HEAP32[(($137)>>2)];
      var $139=(($138)|(0))==0;
      if ($139) { label = 30; break; } else { label = 28; break; }
    case 28: 
      var $141=$fp1;
      var $142=$2;
      var $143=(($142+432)|0);
      var $144=HEAP32[(($143)>>2)];
      var $145=(($141)>>>(0)) > (($144)>>>(0));
      if ($145) { label = 29; break; } else { label = 32; break; }
    case 29: 
      var $147=$fp1;
      var $148=$2;
      var $149=(($148+440)|0);
      var $150=HEAP32[(($149)>>2)];
      var $151=(($147)>>>(0)) <= (($150)>>>(0));
      if ($151) { label = 30; break; } else { label = 32; break; }
    case 30: 
      var $153=$ssize;
      var $154=_sbrk($153);
      $br=$154;
      var $155=$base;
      var $156=(($154)|(0))==(($155)|(0));
      if ($156) { label = 31; break; } else { label = 32; break; }
    case 31: 
      var $158=$base;
      $tbase=$158;
      var $159=$ssize;
      $tsize=$159;
      label = 32; break;
    case 32: 
      label = 33; break;
    case 33: 
      label = 38; break;
    case 34: 
      var $163=$3;
      var $164=$2;
      var $165=(($164+12)|0);
      var $166=HEAP32[(($165)>>2)];
      var $167=((($163)-($166))|0);
      var $168=((($167)+(48))|0);
      var $169=HEAP32[((((5242964)|0))>>2)];
      var $170=((($169)-(1))|0);
      var $171=((($168)+($170))|0);
      var $172=HEAP32[((((5242964)|0))>>2)];
      var $173=((($172)-(1))|0);
      var $174=$173 ^ -1;
      var $175=$171 & $174;
      $ssize=$175;
      var $176=$ssize;
      var $177=(($176)>>>(0)) < 2147483647;
      if ($177) { label = 35; break; } else { label = 37; break; }
    case 35: 
      var $179=$ssize;
      var $180=_sbrk($179);
      $br=$180;
      var $181=$ss;
      var $182=(($181)|0);
      var $183=HEAP32[(($182)>>2)];
      var $184=$ss;
      var $185=(($184+4)|0);
      var $186=HEAP32[(($185)>>2)];
      var $187=(($183+$186)|0);
      var $188=(($180)|(0))==(($187)|(0));
      if ($188) { label = 36; break; } else { label = 37; break; }
    case 36: 
      var $190=$br;
      $tbase=$190;
      var $191=$ssize;
      $tsize=$191;
      label = 37; break;
    case 37: 
      label = 38; break;
    case 38: 
      var $194=$tbase;
      var $195=(($194)|(0))==-1;
      if ($195) { label = 39; break; } else { label = 53; break; }
    case 39: 
      var $197=$br;
      var $198=(($197)|(0))!=-1;
      if ($198) { label = 40; break; } else { label = 49; break; }
    case 40: 
      var $200=$ssize;
      var $201=(($200)>>>(0)) < 2147483647;
      if ($201) { label = 41; break; } else { label = 48; break; }
    case 41: 
      var $203=$ssize;
      var $204=$3;
      var $205=((($204)+(48))|0);
      var $206=(($203)>>>(0)) < (($205)>>>(0));
      if ($206) { label = 42; break; } else { label = 48; break; }
    case 42: 
      var $208=$3;
      var $209=((($208)+(48))|0);
      var $210=$ssize;
      var $211=((($209)-($210))|0);
      var $212=HEAP32[((((5242964)|0))>>2)];
      var $213=((($212)-(1))|0);
      var $214=((($211)+($213))|0);
      var $215=HEAP32[((((5242964)|0))>>2)];
      var $216=((($215)-(1))|0);
      var $217=$216 ^ -1;
      var $218=$214 & $217;
      $esize=$218;
      var $219=$esize;
      var $220=(($219)>>>(0)) < 2147483647;
      if ($220) { label = 43; break; } else { label = 47; break; }
    case 43: 
      var $222=$esize;
      var $223=_sbrk($222);
      $end=$223;
      var $224=$end;
      var $225=(($224)|(0))!=-1;
      if ($225) { label = 44; break; } else { label = 45; break; }
    case 44: 
      var $227=$esize;
      var $228=$ssize;
      var $229=((($228)+($227))|0);
      $ssize=$229;
      label = 46; break;
    case 45: 
      var $231=$ssize;
      var $232=(((-$231))|0);
      var $233=_sbrk($232);
      $br=-1;
      label = 46; break;
    case 46: 
      label = 47; break;
    case 47: 
      label = 48; break;
    case 48: 
      label = 49; break;
    case 49: 
      var $238=$br;
      var $239=(($238)|(0))!=-1;
      if ($239) { label = 50; break; } else { label = 51; break; }
    case 50: 
      var $241=$br;
      $tbase=$241;
      var $242=$ssize;
      $tsize=$242;
      label = 52; break;
    case 51: 
      var $244=$2;
      var $245=(($244+444)|0);
      var $246=HEAP32[(($245)>>2)];
      var $247=$246 | 4;
      HEAP32[(($245)>>2)]=$247;
      label = 52; break;
    case 52: 
      label = 53; break;
    case 53: 
      label = 54; break;
    case 54: 
      var $251=$tbase;
      var $252=(($251)|(0))==-1;
      if ($252) { label = 55; break; } else { label = 64; break; }
    case 55: 
      var $254=$asize;
      var $255=(($254)>>>(0)) < 2147483647;
      if ($255) { label = 56; break; } else { label = 63; break; }
    case 56: 
      $br2=-1;
      $end3=-1;
      var $257=$asize;
      var $258=_sbrk($257);
      $br2=$258;
      var $259=_sbrk(0);
      $end3=$259;
      var $260=$br2;
      var $261=(($260)|(0))!=-1;
      if ($261) { label = 57; break; } else { label = 62; break; }
    case 57: 
      var $263=$end3;
      var $264=(($263)|(0))!=-1;
      if ($264) { label = 58; break; } else { label = 62; break; }
    case 58: 
      var $266=$br2;
      var $267=$end3;
      var $268=(($266)>>>(0)) < (($267)>>>(0));
      if ($268) { label = 59; break; } else { label = 62; break; }
    case 59: 
      var $270=$end3;
      var $271=$br2;
      var $272=$270;
      var $273=$271;
      var $274=((($272)-($273))|0);
      $ssize4=$274;
      var $275=$ssize4;
      var $276=$3;
      var $277=((($276)+(40))|0);
      var $278=(($275)>>>(0)) > (($277)>>>(0));
      if ($278) { label = 60; break; } else { label = 61; break; }
    case 60: 
      var $280=$br2;
      $tbase=$280;
      var $281=$ssize4;
      $tsize=$281;
      label = 61; break;
    case 61: 
      label = 62; break;
    case 62: 
      label = 63; break;
    case 63: 
      label = 64; break;
    case 64: 
      var $286=$tbase;
      var $287=(($286)|(0))!=-1;
      if ($287) { label = 65; break; } else { label = 103; break; }
    case 65: 
      var $289=$tsize;
      var $290=$2;
      var $291=(($290+432)|0);
      var $292=HEAP32[(($291)>>2)];
      var $293=((($292)+($289))|0);
      HEAP32[(($291)>>2)]=$293;
      var $294=$2;
      var $295=(($294+436)|0);
      var $296=HEAP32[(($295)>>2)];
      var $297=(($293)>>>(0)) > (($296)>>>(0));
      if ($297) { label = 66; break; } else { label = 67; break; }
    case 66: 
      var $299=$2;
      var $300=(($299+432)|0);
      var $301=HEAP32[(($300)>>2)];
      var $302=$2;
      var $303=(($302+436)|0);
      HEAP32[(($303)>>2)]=$301;
      label = 67; break;
    case 67: 
      var $305=$2;
      var $306=(($305+24)|0);
      var $307=HEAP32[(($306)>>2)];
      var $308=(($307)|(0))!=0;
      if ($308) { label = 75; break; } else { label = 68; break; }
    case 68: 
      var $310=$2;
      var $311=(($310+16)|0);
      var $312=HEAP32[(($311)>>2)];
      var $313=(($312)|(0))==0;
      if ($313) { label = 70; break; } else { label = 69; break; }
    case 69: 
      var $315=$tbase;
      var $316=$2;
      var $317=(($316+16)|0);
      var $318=HEAP32[(($317)>>2)];
      var $319=(($315)>>>(0)) < (($318)>>>(0));
      if ($319) { label = 70; break; } else { label = 71; break; }
    case 70: 
      var $321=$tbase;
      var $322=$2;
      var $323=(($322+16)|0);
      HEAP32[(($323)>>2)]=$321;
      label = 71; break;
    case 71: 
      var $325=$tbase;
      var $326=$2;
      var $327=(($326+448)|0);
      var $328=(($327)|0);
      HEAP32[(($328)>>2)]=$325;
      var $329=$tsize;
      var $330=$2;
      var $331=(($330+448)|0);
      var $332=(($331+4)|0);
      HEAP32[(($332)>>2)]=$329;
      var $333=$mmap_flag;
      var $334=$2;
      var $335=(($334+448)|0);
      var $336=(($335+12)|0);
      HEAP32[(($336)>>2)]=$333;
      var $337=HEAP32[((((5242956)|0))>>2)];
      var $338=$2;
      var $339=(($338+36)|0);
      HEAP32[(($339)>>2)]=$337;
      var $340=$2;
      var $341=(($340+32)|0);
      HEAP32[(($341)>>2)]=-1;
      var $342=$2;
      _init_bins($342);
      var $343=$2;
      var $344=(($343)|(0))==5243512;
      if ($344) { label = 72; break; } else { label = 73; break; }
    case 72: 
      var $346=$2;
      var $347=$tbase;
      var $348=$347;
      var $349=$tsize;
      var $350=((($349)-(40))|0);
      _init_top($346, $348, $350);
      label = 74; break;
    case 73: 
      var $352=$2;
      var $353=$352;
      var $354=((($353)-(8))|0);
      var $355=$354;
      var $356=$355;
      var $357=$2;
      var $358=$357;
      var $359=((($358)-(8))|0);
      var $360=$359;
      var $361=(($360+4)|0);
      var $362=HEAP32[(($361)>>2)];
      var $363=$362 & -8;
      var $364=(($356+$363)|0);
      var $365=$364;
      $mn=$365;
      var $366=$2;
      var $367=$mn;
      var $368=$tbase;
      var $369=$tsize;
      var $370=(($368+$369)|0);
      var $371=$mn;
      var $372=$371;
      var $373=$370;
      var $374=$372;
      var $375=((($373)-($374))|0);
      var $376=((($375)-(40))|0);
      _init_top($366, $367, $376);
      label = 74; break;
    case 74: 
      label = 100; break;
    case 75: 
      var $379=$2;
      var $380=(($379+448)|0);
      $sp=$380;
      label = 76; break;
    case 76: 
      var $382=$sp;
      var $383=(($382)|(0))!=0;
      if ($383) { label = 77; break; } else { var $395 = 0;label = 78; break; }
    case 77: 
      var $385=$tbase;
      var $386=$sp;
      var $387=(($386)|0);
      var $388=HEAP32[(($387)>>2)];
      var $389=$sp;
      var $390=(($389+4)|0);
      var $391=HEAP32[(($390)>>2)];
      var $392=(($388+$391)|0);
      var $393=(($385)|(0))!=(($392)|(0));
      var $395 = $393;label = 78; break;
    case 78: 
      var $395;
      if ($395) { label = 79; break; } else { label = 80; break; }
    case 79: 
      var $397=$sp;
      var $398=(($397+8)|0);
      var $399=HEAP32[(($398)>>2)];
      $sp=$399;
      label = 76; break;
    case 80: 
      var $401=$sp;
      var $402=(($401)|(0))!=0;
      if ($402) { label = 81; break; } else { label = 86; break; }
    case 81: 
      var $404=$sp;
      var $405=(($404+12)|0);
      var $406=HEAP32[(($405)>>2)];
      var $407=$406 & 8;
      var $408=(($407)|(0))!=0;
      if ($408) { label = 86; break; } else { label = 82; break; }
    case 82: 
      var $410=$sp;
      var $411=(($410+12)|0);
      var $412=HEAP32[(($411)>>2)];
      var $413=$412 & 0;
      var $414=$mmap_flag;
      var $415=(($413)|(0))==(($414)|(0));
      if ($415) { label = 83; break; } else { label = 86; break; }
    case 83: 
      var $417=$2;
      var $418=(($417+24)|0);
      var $419=HEAP32[(($418)>>2)];
      var $420=$419;
      var $421=$sp;
      var $422=(($421)|0);
      var $423=HEAP32[(($422)>>2)];
      var $424=(($420)>>>(0)) >= (($423)>>>(0));
      if ($424) { label = 84; break; } else { label = 86; break; }
    case 84: 
      var $426=$2;
      var $427=(($426+24)|0);
      var $428=HEAP32[(($427)>>2)];
      var $429=$428;
      var $430=$sp;
      var $431=(($430)|0);
      var $432=HEAP32[(($431)>>2)];
      var $433=$sp;
      var $434=(($433+4)|0);
      var $435=HEAP32[(($434)>>2)];
      var $436=(($432+$435)|0);
      var $437=(($429)>>>(0)) < (($436)>>>(0));
      if ($437) { label = 85; break; } else { label = 86; break; }
    case 85: 
      var $439=$tsize;
      var $440=$sp;
      var $441=(($440+4)|0);
      var $442=HEAP32[(($441)>>2)];
      var $443=((($442)+($439))|0);
      HEAP32[(($441)>>2)]=$443;
      var $444=$2;
      var $445=$2;
      var $446=(($445+24)|0);
      var $447=HEAP32[(($446)>>2)];
      var $448=$2;
      var $449=(($448+12)|0);
      var $450=HEAP32[(($449)>>2)];
      var $451=$tsize;
      var $452=((($450)+($451))|0);
      _init_top($444, $447, $452);
      label = 99; break;
    case 86: 
      var $454=$tbase;
      var $455=$2;
      var $456=(($455+16)|0);
      var $457=HEAP32[(($456)>>2)];
      var $458=(($454)>>>(0)) < (($457)>>>(0));
      if ($458) { label = 87; break; } else { label = 88; break; }
    case 87: 
      var $460=$tbase;
      var $461=$2;
      var $462=(($461+16)|0);
      HEAP32[(($462)>>2)]=$460;
      label = 88; break;
    case 88: 
      var $464=$2;
      var $465=(($464+448)|0);
      $sp=$465;
      label = 89; break;
    case 89: 
      var $467=$sp;
      var $468=(($467)|(0))!=0;
      if ($468) { label = 90; break; } else { var $478 = 0;label = 91; break; }
    case 90: 
      var $470=$sp;
      var $471=(($470)|0);
      var $472=HEAP32[(($471)>>2)];
      var $473=$tbase;
      var $474=$tsize;
      var $475=(($473+$474)|0);
      var $476=(($472)|(0))!=(($475)|(0));
      var $478 = $476;label = 91; break;
    case 91: 
      var $478;
      if ($478) { label = 92; break; } else { label = 93; break; }
    case 92: 
      var $480=$sp;
      var $481=(($480+8)|0);
      var $482=HEAP32[(($481)>>2)];
      $sp=$482;
      label = 89; break;
    case 93: 
      var $484=$sp;
      var $485=(($484)|(0))!=0;
      if ($485) { label = 94; break; } else { label = 97; break; }
    case 94: 
      var $487=$sp;
      var $488=(($487+12)|0);
      var $489=HEAP32[(($488)>>2)];
      var $490=$489 & 8;
      var $491=(($490)|(0))!=0;
      if ($491) { label = 97; break; } else { label = 95; break; }
    case 95: 
      var $493=$sp;
      var $494=(($493+12)|0);
      var $495=HEAP32[(($494)>>2)];
      var $496=$495 & 0;
      var $497=$mmap_flag;
      var $498=(($496)|(0))==(($497)|(0));
      if ($498) { label = 96; break; } else { label = 97; break; }
    case 96: 
      var $500=$sp;
      var $501=(($500)|0);
      var $502=HEAP32[(($501)>>2)];
      $oldbase=$502;
      var $503=$tbase;
      var $504=$sp;
      var $505=(($504)|0);
      HEAP32[(($505)>>2)]=$503;
      var $506=$tsize;
      var $507=$sp;
      var $508=(($507+4)|0);
      var $509=HEAP32[(($508)>>2)];
      var $510=((($509)+($506))|0);
      HEAP32[(($508)>>2)]=$510;
      var $511=$2;
      var $512=$tbase;
      var $513=$oldbase;
      var $514=$3;
      var $515=_prepend_alloc($511, $512, $513, $514);
      $1=$515;
      label = 104; break;
    case 97: 
      var $517=$2;
      var $518=$tbase;
      var $519=$tsize;
      var $520=$mmap_flag;
      _add_segment($517, $518, $519, $520);
      label = 98; break;
    case 98: 
      label = 99; break;
    case 99: 
      label = 100; break;
    case 100: 
      var $524=$3;
      var $525=$2;
      var $526=(($525+12)|0);
      var $527=HEAP32[(($526)>>2)];
      var $528=(($524)>>>(0)) < (($527)>>>(0));
      if ($528) { label = 101; break; } else { label = 102; break; }
    case 101: 
      var $530=$3;
      var $531=$2;
      var $532=(($531+12)|0);
      var $533=HEAP32[(($532)>>2)];
      var $534=((($533)-($530))|0);
      HEAP32[(($532)>>2)]=$534;
      $rsize=$534;
      var $535=$2;
      var $536=(($535+24)|0);
      var $537=HEAP32[(($536)>>2)];
      $p=$537;
      var $538=$p;
      var $539=$538;
      var $540=$3;
      var $541=(($539+$540)|0);
      var $542=$541;
      var $543=$2;
      var $544=(($543+24)|0);
      HEAP32[(($544)>>2)]=$542;
      $r=$542;
      var $545=$rsize;
      var $546=$545 | 1;
      var $547=$r;
      var $548=(($547+4)|0);
      HEAP32[(($548)>>2)]=$546;
      var $549=$3;
      var $550=$549 | 1;
      var $551=$550 | 2;
      var $552=$p;
      var $553=(($552+4)|0);
      HEAP32[(($553)>>2)]=$551;
      var $554=$p;
      var $555=$554;
      var $556=(($555+8)|0);
      $1=$556;
      label = 104; break;
    case 102: 
      label = 103; break;
    case 103: 
      var $559=___errno_location();
      HEAP32[(($559)>>2)]=12;
      $1=0;
      label = 104; break;
    case 104: 
      var $561=$1;
      return $561;
    default: assert(0, "bad label: " + label);
  }
}
function _init_mparams() {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $magic;
      var $psize;
      var $gsize;
      var $1=HEAP32[((((5242956)|0))>>2)];
      var $2=(($1)|(0))==0;
      if ($2) { label = 3; break; } else { label = 7; break; }
    case 3: 
      var $4=_sysconf(8);
      $psize=$4;
      var $5=$psize;
      $gsize=$5;
      var $6=$gsize;
      var $7=$gsize;
      var $8=((($7)-(1))|0);
      var $9=$6 & $8;
      var $10=(($9)|(0))!=0;
      if ($10) { label = 5; break; } else { label = 4; break; }
    case 4: 
      var $12=$psize;
      var $13=$psize;
      var $14=((($13)-(1))|0);
      var $15=$12 & $14;
      var $16=(($15)|(0))!=0;
      if ($16) { label = 5; break; } else { label = 6; break; }
    case 5: 
      _abort();
      throw "Reached an unreachable!"
    case 6: 
      var $19=$gsize;
      HEAP32[((((5242964)|0))>>2)]=$19;
      var $20=$psize;
      HEAP32[((((5242960)|0))>>2)]=$20;
      HEAP32[((((5242968)|0))>>2)]=-1;
      HEAP32[((((5242972)|0))>>2)]=2097152;
      HEAP32[((((5242976)|0))>>2)]=0;
      var $21=HEAP32[((((5242976)|0))>>2)];
      HEAP32[((((5243956)|0))>>2)]=$21;
      var $22=_time(0);
      var $23=$22 ^ 1431655765;
      $magic=$23;
      var $24=$magic;
      var $25=$24 | 8;
      $magic=$25;
      var $26=$magic;
      var $27=$26 & -8;
      $magic=$27;
      var $28=$magic;
      HEAP32[((((5242956)|0))>>2)]=$28;
      label = 7; break;
    case 7: 
      return 1;
    default: assert(0, "bad label: " + label);
  }
}
function _init_top($m, $p, $psize) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $offset;
      $1=$m;
      $2=$p;
      $3=$psize;
      var $4=$2;
      var $5=$4;
      var $6=(($5+8)|0);
      var $7=$6;
      var $8=$7 & 7;
      var $9=(($8)|(0))==0;
      if ($9) { label = 3; break; } else { label = 4; break; }
    case 3: 
      var $20 = 0;label = 5; break;
    case 4: 
      var $12=$2;
      var $13=$12;
      var $14=(($13+8)|0);
      var $15=$14;
      var $16=$15 & 7;
      var $17=(((8)-($16))|0);
      var $18=$17 & 7;
      var $20 = $18;label = 5; break;
    case 5: 
      var $20;
      $offset=$20;
      var $21=$2;
      var $22=$21;
      var $23=$offset;
      var $24=(($22+$23)|0);
      var $25=$24;
      $2=$25;
      var $26=$offset;
      var $27=$3;
      var $28=((($27)-($26))|0);
      $3=$28;
      var $29=$2;
      var $30=$1;
      var $31=(($30+24)|0);
      HEAP32[(($31)>>2)]=$29;
      var $32=$3;
      var $33=$1;
      var $34=(($33+12)|0);
      HEAP32[(($34)>>2)]=$32;
      var $35=$3;
      var $36=$35 | 1;
      var $37=$2;
      var $38=(($37+4)|0);
      HEAP32[(($38)>>2)]=$36;
      var $39=$2;
      var $40=$39;
      var $41=$3;
      var $42=(($40+$41)|0);
      var $43=$42;
      var $44=(($43+4)|0);
      HEAP32[(($44)>>2)]=40;
      var $45=HEAP32[((((5242972)|0))>>2)];
      var $46=$1;
      var $47=(($46+28)|0);
      HEAP32[(($47)>>2)]=$45;
      return;
    default: assert(0, "bad label: " + label);
  }
}
function _mmap_alloc($m, $nb) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $mmsize;
      var $fp;
      var $mm;
      var $offset;
      var $psize;
      var $p;
      $2=$m;
      $3=$nb;
      var $4=$3;
      var $5=((($4)+(24))|0);
      var $6=((($5)+(7))|0);
      var $7=HEAP32[((((5242960)|0))>>2)];
      var $8=((($7)-(1))|0);
      var $9=((($6)+($8))|0);
      var $10=HEAP32[((((5242960)|0))>>2)];
      var $11=((($10)-(1))|0);
      var $12=$11 ^ -1;
      var $13=$9 & $12;
      $mmsize=$13;
      var $14=$2;
      var $15=(($14+440)|0);
      var $16=HEAP32[(($15)>>2)];
      var $17=(($16)|(0))!=0;
      if ($17) { label = 3; break; } else { label = 7; break; }
    case 3: 
      var $19=$2;
      var $20=(($19+432)|0);
      var $21=HEAP32[(($20)>>2)];
      var $22=$mmsize;
      var $23=((($21)+($22))|0);
      $fp=$23;
      var $24=$fp;
      var $25=$2;
      var $26=(($25+432)|0);
      var $27=HEAP32[(($26)>>2)];
      var $28=(($24)>>>(0)) <= (($27)>>>(0));
      if ($28) { label = 5; break; } else { label = 4; break; }
    case 4: 
      var $30=$fp;
      var $31=$2;
      var $32=(($31+440)|0);
      var $33=HEAP32[(($32)>>2)];
      var $34=(($30)>>>(0)) > (($33)>>>(0));
      if ($34) { label = 5; break; } else { label = 6; break; }
    case 5: 
      $1=0;
      label = 20; break;
    case 6: 
      label = 7; break;
    case 7: 
      var $38=$mmsize;
      var $39=$3;
      var $40=(($38)>>>(0)) > (($39)>>>(0));
      if ($40) { label = 8; break; } else { label = 19; break; }
    case 8: 
      $mm=-1;
      var $42=$mm;
      var $43=(($42)|(0))!=-1;
      if ($43) { label = 9; break; } else { label = 18; break; }
    case 9: 
      var $45=$mm;
      var $46=(($45+8)|0);
      var $47=$46;
      var $48=$47 & 7;
      var $49=(($48)|(0))==0;
      if ($49) { label = 10; break; } else { label = 11; break; }
    case 10: 
      var $59 = 0;label = 12; break;
    case 11: 
      var $52=$mm;
      var $53=(($52+8)|0);
      var $54=$53;
      var $55=$54 & 7;
      var $56=(((8)-($55))|0);
      var $57=$56 & 7;
      var $59 = $57;label = 12; break;
    case 12: 
      var $59;
      $offset=$59;
      var $60=$mmsize;
      var $61=$offset;
      var $62=((($60)-($61))|0);
      var $63=((($62)-(16))|0);
      $psize=$63;
      var $64=$mm;
      var $65=$offset;
      var $66=(($64+$65)|0);
      var $67=$66;
      $p=$67;
      var $68=$offset;
      var $69=$p;
      var $70=(($69)|0);
      HEAP32[(($70)>>2)]=$68;
      var $71=$psize;
      var $72=$p;
      var $73=(($72+4)|0);
      HEAP32[(($73)>>2)]=$71;
      var $74=$p;
      var $75=$74;
      var $76=$psize;
      var $77=(($75+$76)|0);
      var $78=$77;
      var $79=(($78+4)|0);
      HEAP32[(($79)>>2)]=7;
      var $80=$p;
      var $81=$80;
      var $82=$psize;
      var $83=((($82)+(4))|0);
      var $84=(($81+$83)|0);
      var $85=$84;
      var $86=(($85+4)|0);
      HEAP32[(($86)>>2)]=0;
      var $87=$2;
      var $88=(($87+16)|0);
      var $89=HEAP32[(($88)>>2)];
      var $90=(($89)|(0))==0;
      if ($90) { label = 14; break; } else { label = 13; break; }
    case 13: 
      var $92=$mm;
      var $93=$2;
      var $94=(($93+16)|0);
      var $95=HEAP32[(($94)>>2)];
      var $96=(($92)>>>(0)) < (($95)>>>(0));
      if ($96) { label = 14; break; } else { label = 15; break; }
    case 14: 
      var $98=$mm;
      var $99=$2;
      var $100=(($99+16)|0);
      HEAP32[(($100)>>2)]=$98;
      label = 15; break;
    case 15: 
      var $102=$mmsize;
      var $103=$2;
      var $104=(($103+432)|0);
      var $105=HEAP32[(($104)>>2)];
      var $106=((($105)+($102))|0);
      HEAP32[(($104)>>2)]=$106;
      var $107=$2;
      var $108=(($107+436)|0);
      var $109=HEAP32[(($108)>>2)];
      var $110=(($106)>>>(0)) > (($109)>>>(0));
      if ($110) { label = 16; break; } else { label = 17; break; }
    case 16: 
      var $112=$2;
      var $113=(($112+432)|0);
      var $114=HEAP32[(($113)>>2)];
      var $115=$2;
      var $116=(($115+436)|0);
      HEAP32[(($116)>>2)]=$114;
      label = 17; break;
    case 17: 
      var $118=$p;
      var $119=$118;
      var $120=(($119+8)|0);
      $1=$120;
      label = 20; break;
    case 18: 
      label = 19; break;
    case 19: 
      $1=0;
      label = 20; break;
    case 20: 
      var $124=$1;
      return $124;
    default: assert(0, "bad label: " + label);
  }
}
function _init_bins($m) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $i;
      var $bin;
      $1=$m;
      $i=0;
      label = 3; break;
    case 3: 
      var $3=$i;
      var $4=(($3)>>>(0)) < 32;
      if ($4) { label = 4; break; } else { label = 6; break; }
    case 4: 
      var $6=$i;
      var $7=$6 << 1;
      var $8=$1;
      var $9=(($8+40)|0);
      var $10=(($9+($7<<2))|0);
      var $11=$10;
      var $12=$11;
      $bin=$12;
      var $13=$bin;
      var $14=$bin;
      var $15=(($14+12)|0);
      HEAP32[(($15)>>2)]=$13;
      var $16=$bin;
      var $17=(($16+8)|0);
      HEAP32[(($17)>>2)]=$13;
      label = 5; break;
    case 5: 
      var $19=$i;
      var $20=((($19)+(1))|0);
      $i=$20;
      label = 3; break;
    case 6: 
      return;
    default: assert(0, "bad label: " + label);
  }
}
function _prepend_alloc($m, $newbase, $oldbase, $nb) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $4;
      var $p;
      var $oldfirst;
      var $psize;
      var $q;
      var $qsize;
      var $tsize;
      var $dsize;
      var $nsize;
      var $F;
      var $B;
      var $I;
      var $TP;
      var $XP;
      var $R;
      var $F1;
      var $RP;
      var $CP;
      var $H;
      var $C0;
      var $C1;
      var $I2;
      var $B3;
      var $F4;
      var $TP5;
      var $H6;
      var $I7;
      var $X;
      var $Y;
      var $N;
      var $K;
      var $T;
      var $K8;
      var $C;
      var $F9;
      $1=$m;
      $2=$newbase;
      $3=$oldbase;
      $4=$nb;
      var $5=$2;
      var $6=$2;
      var $7=(($6+8)|0);
      var $8=$7;
      var $9=$8 & 7;
      var $10=(($9)|(0))==0;
      if ($10) { label = 3; break; } else { label = 4; break; }
    case 3: 
      var $20 = 0;label = 5; break;
    case 4: 
      var $13=$2;
      var $14=(($13+8)|0);
      var $15=$14;
      var $16=$15 & 7;
      var $17=(((8)-($16))|0);
      var $18=$17 & 7;
      var $20 = $18;label = 5; break;
    case 5: 
      var $20;
      var $21=(($5+$20)|0);
      var $22=$21;
      $p=$22;
      var $23=$3;
      var $24=$3;
      var $25=(($24+8)|0);
      var $26=$25;
      var $27=$26 & 7;
      var $28=(($27)|(0))==0;
      if ($28) { label = 6; break; } else { label = 7; break; }
    case 6: 
      var $38 = 0;label = 8; break;
    case 7: 
      var $31=$3;
      var $32=(($31+8)|0);
      var $33=$32;
      var $34=$33 & 7;
      var $35=(((8)-($34))|0);
      var $36=$35 & 7;
      var $38 = $36;label = 8; break;
    case 8: 
      var $38;
      var $39=(($23+$38)|0);
      var $40=$39;
      $oldfirst=$40;
      var $41=$oldfirst;
      var $42=$41;
      var $43=$p;
      var $44=$43;
      var $45=$42;
      var $46=$44;
      var $47=((($45)-($46))|0);
      $psize=$47;
      var $48=$p;
      var $49=$48;
      var $50=$4;
      var $51=(($49+$50)|0);
      var $52=$51;
      $q=$52;
      var $53=$psize;
      var $54=$4;
      var $55=((($53)-($54))|0);
      $qsize=$55;
      var $56=$4;
      var $57=$56 | 1;
      var $58=$57 | 2;
      var $59=$p;
      var $60=(($59+4)|0);
      HEAP32[(($60)>>2)]=$58;
      var $61=$oldfirst;
      var $62=$1;
      var $63=(($62+24)|0);
      var $64=HEAP32[(($63)>>2)];
      var $65=(($61)|(0))==(($64)|(0));
      if ($65) { label = 9; break; } else { label = 10; break; }
    case 9: 
      var $67=$qsize;
      var $68=$1;
      var $69=(($68+12)|0);
      var $70=HEAP32[(($69)>>2)];
      var $71=((($70)+($67))|0);
      HEAP32[(($69)>>2)]=$71;
      $tsize=$71;
      var $72=$q;
      var $73=$1;
      var $74=(($73+24)|0);
      HEAP32[(($74)>>2)]=$72;
      var $75=$tsize;
      var $76=$75 | 1;
      var $77=$q;
      var $78=(($77+4)|0);
      HEAP32[(($78)>>2)]=$76;
      label = 119; break;
    case 10: 
      var $80=$oldfirst;
      var $81=$1;
      var $82=(($81+20)|0);
      var $83=HEAP32[(($82)>>2)];
      var $84=(($80)|(0))==(($83)|(0));
      if ($84) { label = 11; break; } else { label = 12; break; }
    case 11: 
      var $86=$qsize;
      var $87=$1;
      var $88=(($87+8)|0);
      var $89=HEAP32[(($88)>>2)];
      var $90=((($89)+($86))|0);
      HEAP32[(($88)>>2)]=$90;
      $dsize=$90;
      var $91=$q;
      var $92=$1;
      var $93=(($92+20)|0);
      HEAP32[(($93)>>2)]=$91;
      var $94=$dsize;
      var $95=$94 | 1;
      var $96=$q;
      var $97=(($96+4)|0);
      HEAP32[(($97)>>2)]=$95;
      var $98=$dsize;
      var $99=$q;
      var $100=$99;
      var $101=$dsize;
      var $102=(($100+$101)|0);
      var $103=$102;
      var $104=(($103)|0);
      HEAP32[(($104)>>2)]=$98;
      label = 118; break;
    case 12: 
      var $106=$oldfirst;
      var $107=(($106+4)|0);
      var $108=HEAP32[(($107)>>2)];
      var $109=$108 & 3;
      var $110=(($109)|(0))!=1;
      if ($110) { label = 82; break; } else { label = 13; break; }
    case 13: 
      var $112=$oldfirst;
      var $113=(($112+4)|0);
      var $114=HEAP32[(($113)>>2)];
      var $115=$114 & -8;
      $nsize=$115;
      var $116=$nsize;
      var $117=$116 >>> 3;
      var $118=(($117)>>>(0)) < 32;
      if ($118) { label = 14; break; } else { label = 32; break; }
    case 14: 
      var $120=$oldfirst;
      var $121=(($120+8)|0);
      var $122=HEAP32[(($121)>>2)];
      $F=$122;
      var $123=$oldfirst;
      var $124=(($123+12)|0);
      var $125=HEAP32[(($124)>>2)];
      $B=$125;
      var $126=$nsize;
      var $127=$126 >>> 3;
      $I=$127;
      var $128=$F;
      var $129=$I;
      var $130=$129 << 1;
      var $131=$1;
      var $132=(($131+40)|0);
      var $133=(($132+($130<<2))|0);
      var $134=$133;
      var $135=$134;
      var $136=(($128)|(0))==(($135)|(0));
      if ($136) { var $153 = 1;label = 18; break; } else { label = 15; break; }
    case 15: 
      var $138=$F;
      var $139=$138;
      var $140=$1;
      var $141=(($140+16)|0);
      var $142=HEAP32[(($141)>>2)];
      var $143=(($139)>>>(0)) >= (($142)>>>(0));
      if ($143) { label = 16; break; } else { var $151 = 0;label = 17; break; }
    case 16: 
      var $145=$F;
      var $146=(($145+12)|0);
      var $147=HEAP32[(($146)>>2)];
      var $148=$oldfirst;
      var $149=(($147)|(0))==(($148)|(0));
      var $151 = $149;label = 17; break;
    case 17: 
      var $151;
      var $153 = $151;label = 18; break;
    case 18: 
      var $153;
      var $154=(($153)&(1));
      var $155=($154);
      var $156=(($155)|(0))!=0;
      if ($156) { label = 19; break; } else { label = 30; break; }
    case 19: 
      var $158=$B;
      var $159=$F;
      var $160=(($158)|(0))==(($159)|(0));
      if ($160) { label = 20; break; } else { label = 21; break; }
    case 20: 
      var $162=$I;
      var $163=1 << $162;
      var $164=$163 ^ -1;
      var $165=$1;
      var $166=(($165)|0);
      var $167=HEAP32[(($166)>>2)];
      var $168=$167 & $164;
      HEAP32[(($166)>>2)]=$168;
      label = 29; break;
    case 21: 
      var $170=$B;
      var $171=$I;
      var $172=$171 << 1;
      var $173=$1;
      var $174=(($173+40)|0);
      var $175=(($174+($172<<2))|0);
      var $176=$175;
      var $177=$176;
      var $178=(($170)|(0))==(($177)|(0));
      if ($178) { var $195 = 1;label = 25; break; } else { label = 22; break; }
    case 22: 
      var $180=$B;
      var $181=$180;
      var $182=$1;
      var $183=(($182+16)|0);
      var $184=HEAP32[(($183)>>2)];
      var $185=(($181)>>>(0)) >= (($184)>>>(0));
      if ($185) { label = 23; break; } else { var $193 = 0;label = 24; break; }
    case 23: 
      var $187=$B;
      var $188=(($187+8)|0);
      var $189=HEAP32[(($188)>>2)];
      var $190=$oldfirst;
      var $191=(($189)|(0))==(($190)|(0));
      var $193 = $191;label = 24; break;
    case 24: 
      var $193;
      var $195 = $193;label = 25; break;
    case 25: 
      var $195;
      var $196=(($195)&(1));
      var $197=($196);
      var $198=(($197)|(0))!=0;
      if ($198) { label = 26; break; } else { label = 27; break; }
    case 26: 
      var $200=$B;
      var $201=$F;
      var $202=(($201+12)|0);
      HEAP32[(($202)>>2)]=$200;
      var $203=$F;
      var $204=$B;
      var $205=(($204+8)|0);
      HEAP32[(($205)>>2)]=$203;
      label = 28; break;
    case 27: 
      _abort();
      throw "Reached an unreachable!"
    case 28: 
      label = 29; break;
    case 29: 
      label = 31; break;
    case 30: 
      _abort();
      throw "Reached an unreachable!"
    case 31: 
      label = 81; break;
    case 32: 
      var $212=$oldfirst;
      var $213=$212;
      $TP=$213;
      var $214=$TP;
      var $215=(($214+24)|0);
      var $216=HEAP32[(($215)>>2)];
      $XP=$216;
      var $217=$TP;
      var $218=(($217+12)|0);
      var $219=HEAP32[(($218)>>2)];
      var $220=$TP;
      var $221=(($219)|(0))!=(($220)|(0));
      if ($221) { label = 33; break; } else { label = 40; break; }
    case 33: 
      var $223=$TP;
      var $224=(($223+8)|0);
      var $225=HEAP32[(($224)>>2)];
      $F1=$225;
      var $226=$TP;
      var $227=(($226+12)|0);
      var $228=HEAP32[(($227)>>2)];
      $R=$228;
      var $229=$F1;
      var $230=$229;
      var $231=$1;
      var $232=(($231+16)|0);
      var $233=HEAP32[(($232)>>2)];
      var $234=(($230)>>>(0)) >= (($233)>>>(0));
      if ($234) { label = 34; break; } else { var $248 = 0;label = 36; break; }
    case 34: 
      var $236=$F1;
      var $237=(($236+12)|0);
      var $238=HEAP32[(($237)>>2)];
      var $239=$TP;
      var $240=(($238)|(0))==(($239)|(0));
      if ($240) { label = 35; break; } else { var $248 = 0;label = 36; break; }
    case 35: 
      var $242=$R;
      var $243=(($242+8)|0);
      var $244=HEAP32[(($243)>>2)];
      var $245=$TP;
      var $246=(($244)|(0))==(($245)|(0));
      var $248 = $246;label = 36; break;
    case 36: 
      var $248;
      var $249=(($248)&(1));
      var $250=($249);
      var $251=(($250)|(0))!=0;
      if ($251) { label = 37; break; } else { label = 38; break; }
    case 37: 
      var $253=$R;
      var $254=$F1;
      var $255=(($254+12)|0);
      HEAP32[(($255)>>2)]=$253;
      var $256=$F1;
      var $257=$R;
      var $258=(($257+8)|0);
      HEAP32[(($258)>>2)]=$256;
      label = 39; break;
    case 38: 
      _abort();
      throw "Reached an unreachable!"
    case 39: 
      label = 52; break;
    case 40: 
      var $262=$TP;
      var $263=(($262+16)|0);
      var $264=(($263+4)|0);
      $RP=$264;
      var $265=HEAP32[(($264)>>2)];
      $R=$265;
      var $266=(($265)|(0))!=0;
      if ($266) { label = 42; break; } else { label = 41; break; }
    case 41: 
      var $268=$TP;
      var $269=(($268+16)|0);
      var $270=(($269)|0);
      $RP=$270;
      var $271=HEAP32[(($270)>>2)];
      $R=$271;
      var $272=(($271)|(0))!=0;
      if ($272) { label = 42; break; } else { label = 51; break; }
    case 42: 
      label = 43; break;
    case 43: 
      var $275=$R;
      var $276=(($275+16)|0);
      var $277=(($276+4)|0);
      $CP=$277;
      var $278=HEAP32[(($277)>>2)];
      var $279=(($278)|(0))!=0;
      if ($279) { var $287 = 1;label = 45; break; } else { label = 44; break; }
    case 44: 
      var $281=$R;
      var $282=(($281+16)|0);
      var $283=(($282)|0);
      $CP=$283;
      var $284=HEAP32[(($283)>>2)];
      var $285=(($284)|(0))!=0;
      var $287 = $285;label = 45; break;
    case 45: 
      var $287;
      if ($287) { label = 46; break; } else { label = 47; break; }
    case 46: 
      var $289=$CP;
      $RP=$289;
      var $290=HEAP32[(($289)>>2)];
      $R=$290;
      label = 43; break;
    case 47: 
      var $292=$RP;
      var $293=$292;
      var $294=$1;
      var $295=(($294+16)|0);
      var $296=HEAP32[(($295)>>2)];
      var $297=(($293)>>>(0)) >= (($296)>>>(0));
      var $298=(($297)&(1));
      var $299=($298);
      var $300=(($299)|(0))!=0;
      if ($300) { label = 48; break; } else { label = 49; break; }
    case 48: 
      var $302=$RP;
      HEAP32[(($302)>>2)]=0;
      label = 50; break;
    case 49: 
      _abort();
      throw "Reached an unreachable!"
    case 50: 
      label = 51; break;
    case 51: 
      label = 52; break;
    case 52: 
      var $307=$XP;
      var $308=(($307)|(0))!=0;
      if ($308) { label = 53; break; } else { label = 80; break; }
    case 53: 
      var $310=$TP;
      var $311=(($310+28)|0);
      var $312=HEAP32[(($311)>>2)];
      var $313=$1;
      var $314=(($313+304)|0);
      var $315=(($314+($312<<2))|0);
      $H=$315;
      var $316=$TP;
      var $317=$H;
      var $318=HEAP32[(($317)>>2)];
      var $319=(($316)|(0))==(($318)|(0));
      if ($319) { label = 54; break; } else { label = 57; break; }
    case 54: 
      var $321=$R;
      var $322=$H;
      HEAP32[(($322)>>2)]=$321;
      var $323=(($321)|(0))==0;
      if ($323) { label = 55; break; } else { label = 56; break; }
    case 55: 
      var $325=$TP;
      var $326=(($325+28)|0);
      var $327=HEAP32[(($326)>>2)];
      var $328=1 << $327;
      var $329=$328 ^ -1;
      var $330=$1;
      var $331=(($330+4)|0);
      var $332=HEAP32[(($331)>>2)];
      var $333=$332 & $329;
      HEAP32[(($331)>>2)]=$333;
      label = 56; break;
    case 56: 
      label = 64; break;
    case 57: 
      var $336=$XP;
      var $337=$336;
      var $338=$1;
      var $339=(($338+16)|0);
      var $340=HEAP32[(($339)>>2)];
      var $341=(($337)>>>(0)) >= (($340)>>>(0));
      var $342=(($341)&(1));
      var $343=($342);
      var $344=(($343)|(0))!=0;
      if ($344) { label = 58; break; } else { label = 62; break; }
    case 58: 
      var $346=$XP;
      var $347=(($346+16)|0);
      var $348=(($347)|0);
      var $349=HEAP32[(($348)>>2)];
      var $350=$TP;
      var $351=(($349)|(0))==(($350)|(0));
      if ($351) { label = 59; break; } else { label = 60; break; }
    case 59: 
      var $353=$R;
      var $354=$XP;
      var $355=(($354+16)|0);
      var $356=(($355)|0);
      HEAP32[(($356)>>2)]=$353;
      label = 61; break;
    case 60: 
      var $358=$R;
      var $359=$XP;
      var $360=(($359+16)|0);
      var $361=(($360+4)|0);
      HEAP32[(($361)>>2)]=$358;
      label = 61; break;
    case 61: 
      label = 63; break;
    case 62: 
      _abort();
      throw "Reached an unreachable!"
    case 63: 
      label = 64; break;
    case 64: 
      var $366=$R;
      var $367=(($366)|(0))!=0;
      if ($367) { label = 65; break; } else { label = 79; break; }
    case 65: 
      var $369=$R;
      var $370=$369;
      var $371=$1;
      var $372=(($371+16)|0);
      var $373=HEAP32[(($372)>>2)];
      var $374=(($370)>>>(0)) >= (($373)>>>(0));
      var $375=(($374)&(1));
      var $376=($375);
      var $377=(($376)|(0))!=0;
      if ($377) { label = 66; break; } else { label = 77; break; }
    case 66: 
      var $379=$XP;
      var $380=$R;
      var $381=(($380+24)|0);
      HEAP32[(($381)>>2)]=$379;
      var $382=$TP;
      var $383=(($382+16)|0);
      var $384=(($383)|0);
      var $385=HEAP32[(($384)>>2)];
      $C0=$385;
      var $386=(($385)|(0))!=0;
      if ($386) { label = 67; break; } else { label = 71; break; }
    case 67: 
      var $388=$C0;
      var $389=$388;
      var $390=$1;
      var $391=(($390+16)|0);
      var $392=HEAP32[(($391)>>2)];
      var $393=(($389)>>>(0)) >= (($392)>>>(0));
      var $394=(($393)&(1));
      var $395=($394);
      var $396=(($395)|(0))!=0;
      if ($396) { label = 68; break; } else { label = 69; break; }
    case 68: 
      var $398=$C0;
      var $399=$R;
      var $400=(($399+16)|0);
      var $401=(($400)|0);
      HEAP32[(($401)>>2)]=$398;
      var $402=$R;
      var $403=$C0;
      var $404=(($403+24)|0);
      HEAP32[(($404)>>2)]=$402;
      label = 70; break;
    case 69: 
      _abort();
      throw "Reached an unreachable!"
    case 70: 
      label = 71; break;
    case 71: 
      var $408=$TP;
      var $409=(($408+16)|0);
      var $410=(($409+4)|0);
      var $411=HEAP32[(($410)>>2)];
      $C1=$411;
      var $412=(($411)|(0))!=0;
      if ($412) { label = 72; break; } else { label = 76; break; }
    case 72: 
      var $414=$C1;
      var $415=$414;
      var $416=$1;
      var $417=(($416+16)|0);
      var $418=HEAP32[(($417)>>2)];
      var $419=(($415)>>>(0)) >= (($418)>>>(0));
      var $420=(($419)&(1));
      var $421=($420);
      var $422=(($421)|(0))!=0;
      if ($422) { label = 73; break; } else { label = 74; break; }
    case 73: 
      var $424=$C1;
      var $425=$R;
      var $426=(($425+16)|0);
      var $427=(($426+4)|0);
      HEAP32[(($427)>>2)]=$424;
      var $428=$R;
      var $429=$C1;
      var $430=(($429+24)|0);
      HEAP32[(($430)>>2)]=$428;
      label = 75; break;
    case 74: 
      _abort();
      throw "Reached an unreachable!"
    case 75: 
      label = 76; break;
    case 76: 
      label = 78; break;
    case 77: 
      _abort();
      throw "Reached an unreachable!"
    case 78: 
      label = 79; break;
    case 79: 
      label = 80; break;
    case 80: 
      label = 81; break;
    case 81: 
      var $439=$oldfirst;
      var $440=$439;
      var $441=$nsize;
      var $442=(($440+$441)|0);
      var $443=$442;
      $oldfirst=$443;
      var $444=$nsize;
      var $445=$qsize;
      var $446=((($445)+($444))|0);
      $qsize=$446;
      label = 82; break;
    case 82: 
      var $448=$oldfirst;
      var $449=(($448+4)|0);
      var $450=HEAP32[(($449)>>2)];
      var $451=$450 & -2;
      HEAP32[(($449)>>2)]=$451;
      var $452=$qsize;
      var $453=$452 | 1;
      var $454=$q;
      var $455=(($454+4)|0);
      HEAP32[(($455)>>2)]=$453;
      var $456=$qsize;
      var $457=$q;
      var $458=$457;
      var $459=$qsize;
      var $460=(($458+$459)|0);
      var $461=$460;
      var $462=(($461)|0);
      HEAP32[(($462)>>2)]=$456;
      var $463=$qsize;
      var $464=$463 >>> 3;
      var $465=(($464)>>>(0)) < 32;
      if ($465) { label = 83; break; } else { label = 90; break; }
    case 83: 
      var $467=$qsize;
      var $468=$467 >>> 3;
      $I2=$468;
      var $469=$I2;
      var $470=$469 << 1;
      var $471=$1;
      var $472=(($471+40)|0);
      var $473=(($472+($470<<2))|0);
      var $474=$473;
      var $475=$474;
      $B3=$475;
      var $476=$B3;
      $F4=$476;
      var $477=$1;
      var $478=(($477)|0);
      var $479=HEAP32[(($478)>>2)];
      var $480=$I2;
      var $481=1 << $480;
      var $482=$479 & $481;
      var $483=(($482)|(0))!=0;
      if ($483) { label = 85; break; } else { label = 84; break; }
    case 84: 
      var $485=$I2;
      var $486=1 << $485;
      var $487=$1;
      var $488=(($487)|0);
      var $489=HEAP32[(($488)>>2)];
      var $490=$489 | $486;
      HEAP32[(($488)>>2)]=$490;
      label = 89; break;
    case 85: 
      var $492=$B3;
      var $493=(($492+8)|0);
      var $494=HEAP32[(($493)>>2)];
      var $495=$494;
      var $496=$1;
      var $497=(($496+16)|0);
      var $498=HEAP32[(($497)>>2)];
      var $499=(($495)>>>(0)) >= (($498)>>>(0));
      var $500=(($499)&(1));
      var $501=($500);
      var $502=(($501)|(0))!=0;
      if ($502) { label = 86; break; } else { label = 87; break; }
    case 86: 
      var $504=$B3;
      var $505=(($504+8)|0);
      var $506=HEAP32[(($505)>>2)];
      $F4=$506;
      label = 88; break;
    case 87: 
      _abort();
      throw "Reached an unreachable!"
    case 88: 
      label = 89; break;
    case 89: 
      var $510=$q;
      var $511=$B3;
      var $512=(($511+8)|0);
      HEAP32[(($512)>>2)]=$510;
      var $513=$q;
      var $514=$F4;
      var $515=(($514+12)|0);
      HEAP32[(($515)>>2)]=$513;
      var $516=$F4;
      var $517=$q;
      var $518=(($517+8)|0);
      HEAP32[(($518)>>2)]=$516;
      var $519=$B3;
      var $520=$q;
      var $521=(($520+12)|0);
      HEAP32[(($521)>>2)]=$519;
      label = 117; break;
    case 90: 
      var $523=$q;
      var $524=$523;
      $TP5=$524;
      var $525=$qsize;
      var $526=$525 >>> 8;
      $X=$526;
      var $527=$X;
      var $528=(($527)|(0))==0;
      if ($528) { label = 91; break; } else { label = 92; break; }
    case 91: 
      $I7=0;
      label = 96; break;
    case 92: 
      var $531=$X;
      var $532=(($531)>>>(0)) > 65535;
      if ($532) { label = 93; break; } else { label = 94; break; }
    case 93: 
      $I7=31;
      label = 95; break;
    case 94: 
      var $535=$X;
      $Y=$535;
      var $536=$Y;
      var $537=((($536)-(256))|0);
      var $538=$537 >>> 16;
      var $539=$538 & 8;
      $N=$539;
      var $540=$N;
      var $541=$Y;
      var $542=$541 << $540;
      $Y=$542;
      var $543=((($542)-(4096))|0);
      var $544=$543 >>> 16;
      var $545=$544 & 4;
      $K=$545;
      var $546=$K;
      var $547=$N;
      var $548=((($547)+($546))|0);
      $N=$548;
      var $549=$K;
      var $550=$Y;
      var $551=$550 << $549;
      $Y=$551;
      var $552=((($551)-(16384))|0);
      var $553=$552 >>> 16;
      var $554=$553 & 2;
      $K=$554;
      var $555=$N;
      var $556=((($555)+($554))|0);
      $N=$556;
      var $557=$N;
      var $558=(((14)-($557))|0);
      var $559=$K;
      var $560=$Y;
      var $561=$560 << $559;
      $Y=$561;
      var $562=$561 >>> 15;
      var $563=((($558)+($562))|0);
      $K=$563;
      var $564=$K;
      var $565=$564 << 1;
      var $566=$qsize;
      var $567=$K;
      var $568=((($567)+(7))|0);
      var $569=$566 >>> (($568)>>>(0));
      var $570=$569 & 1;
      var $571=((($565)+($570))|0);
      $I7=$571;
      label = 95; break;
    case 95: 
      label = 96; break;
    case 96: 
      var $574=$I7;
      var $575=$1;
      var $576=(($575+304)|0);
      var $577=(($576+($574<<2))|0);
      $H6=$577;
      var $578=$I7;
      var $579=$TP5;
      var $580=(($579+28)|0);
      HEAP32[(($580)>>2)]=$578;
      var $581=$TP5;
      var $582=(($581+16)|0);
      var $583=(($582+4)|0);
      HEAP32[(($583)>>2)]=0;
      var $584=$TP5;
      var $585=(($584+16)|0);
      var $586=(($585)|0);
      HEAP32[(($586)>>2)]=0;
      var $587=$1;
      var $588=(($587+4)|0);
      var $589=HEAP32[(($588)>>2)];
      var $590=$I7;
      var $591=1 << $590;
      var $592=$589 & $591;
      var $593=(($592)|(0))!=0;
      if ($593) { label = 98; break; } else { label = 97; break; }
    case 97: 
      var $595=$I7;
      var $596=1 << $595;
      var $597=$1;
      var $598=(($597+4)|0);
      var $599=HEAP32[(($598)>>2)];
      var $600=$599 | $596;
      HEAP32[(($598)>>2)]=$600;
      var $601=$TP5;
      var $602=$H6;
      HEAP32[(($602)>>2)]=$601;
      var $603=$H6;
      var $604=$603;
      var $605=$TP5;
      var $606=(($605+24)|0);
      HEAP32[(($606)>>2)]=$604;
      var $607=$TP5;
      var $608=$TP5;
      var $609=(($608+12)|0);
      HEAP32[(($609)>>2)]=$607;
      var $610=$TP5;
      var $611=(($610+8)|0);
      HEAP32[(($611)>>2)]=$607;
      label = 116; break;
    case 98: 
      var $613=$H6;
      var $614=HEAP32[(($613)>>2)];
      $T=$614;
      var $615=$qsize;
      var $616=$I7;
      var $617=(($616)|(0))==31;
      if ($617) { label = 99; break; } else { label = 100; break; }
    case 99: 
      var $626 = 0;label = 101; break;
    case 100: 
      var $620=$I7;
      var $621=$620 >>> 1;
      var $622=((($621)+(8))|0);
      var $623=((($622)-(2))|0);
      var $624=(((31)-($623))|0);
      var $626 = $624;label = 101; break;
    case 101: 
      var $626;
      var $627=$615 << $626;
      $K8=$627;
      label = 102; break;
    case 102: 
      var $629=$T;
      var $630=(($629+4)|0);
      var $631=HEAP32[(($630)>>2)];
      var $632=$631 & -8;
      var $633=$qsize;
      var $634=(($632)|(0))!=(($633)|(0));
      if ($634) { label = 103; break; } else { label = 109; break; }
    case 103: 
      var $636=$K8;
      var $637=$636 >>> 31;
      var $638=$637 & 1;
      var $639=$T;
      var $640=(($639+16)|0);
      var $641=(($640+($638<<2))|0);
      $C=$641;
      var $642=$K8;
      var $643=$642 << 1;
      $K8=$643;
      var $644=$C;
      var $645=HEAP32[(($644)>>2)];
      var $646=(($645)|(0))!=0;
      if ($646) { label = 104; break; } else { label = 105; break; }
    case 104: 
      var $648=$C;
      var $649=HEAP32[(($648)>>2)];
      $T=$649;
      label = 108; break;
    case 105: 
      var $651=$C;
      var $652=$651;
      var $653=$1;
      var $654=(($653+16)|0);
      var $655=HEAP32[(($654)>>2)];
      var $656=(($652)>>>(0)) >= (($655)>>>(0));
      var $657=(($656)&(1));
      var $658=($657);
      var $659=(($658)|(0))!=0;
      if ($659) { label = 106; break; } else { label = 107; break; }
    case 106: 
      var $661=$TP5;
      var $662=$C;
      HEAP32[(($662)>>2)]=$661;
      var $663=$T;
      var $664=$TP5;
      var $665=(($664+24)|0);
      HEAP32[(($665)>>2)]=$663;
      var $666=$TP5;
      var $667=$TP5;
      var $668=(($667+12)|0);
      HEAP32[(($668)>>2)]=$666;
      var $669=$TP5;
      var $670=(($669+8)|0);
      HEAP32[(($670)>>2)]=$666;
      label = 115; break;
    case 107: 
      _abort();
      throw "Reached an unreachable!"
    case 108: 
      label = 114; break;
    case 109: 
      var $674=$T;
      var $675=(($674+8)|0);
      var $676=HEAP32[(($675)>>2)];
      $F9=$676;
      var $677=$T;
      var $678=$677;
      var $679=$1;
      var $680=(($679+16)|0);
      var $681=HEAP32[(($680)>>2)];
      var $682=(($678)>>>(0)) >= (($681)>>>(0));
      if ($682) { label = 110; break; } else { var $691 = 0;label = 111; break; }
    case 110: 
      var $684=$F9;
      var $685=$684;
      var $686=$1;
      var $687=(($686+16)|0);
      var $688=HEAP32[(($687)>>2)];
      var $689=(($685)>>>(0)) >= (($688)>>>(0));
      var $691 = $689;label = 111; break;
    case 111: 
      var $691;
      var $692=(($691)&(1));
      var $693=($692);
      var $694=(($693)|(0))!=0;
      if ($694) { label = 112; break; } else { label = 113; break; }
    case 112: 
      var $696=$TP5;
      var $697=$F9;
      var $698=(($697+12)|0);
      HEAP32[(($698)>>2)]=$696;
      var $699=$T;
      var $700=(($699+8)|0);
      HEAP32[(($700)>>2)]=$696;
      var $701=$F9;
      var $702=$TP5;
      var $703=(($702+8)|0);
      HEAP32[(($703)>>2)]=$701;
      var $704=$T;
      var $705=$TP5;
      var $706=(($705+12)|0);
      HEAP32[(($706)>>2)]=$704;
      var $707=$TP5;
      var $708=(($707+24)|0);
      HEAP32[(($708)>>2)]=0;
      label = 115; break;
    case 113: 
      _abort();
      throw "Reached an unreachable!"
    case 114: 
      label = 102; break;
    case 115: 
      label = 116; break;
    case 116: 
      label = 117; break;
    case 117: 
      label = 118; break;
    case 118: 
      label = 119; break;
    case 119: 
      var $716=$p;
      var $717=$716;
      var $718=(($717+8)|0);
      return $718;
    default: assert(0, "bad label: " + label);
  }
}
function _add_segment($m, $tbase, $tsize, $mmapped) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $4;
      var $old_top;
      var $oldsp;
      var $old_end;
      var $ssize;
      var $rawsp;
      var $offset;
      var $asp;
      var $csp;
      var $sp;
      var $ss;
      var $tnext;
      var $p;
      var $nfences;
      var $nextp;
      var $q;
      var $psize;
      var $tn;
      var $I;
      var $B;
      var $F;
      var $TP;
      var $H;
      var $I1;
      var $X;
      var $Y;
      var $N;
      var $K;
      var $T;
      var $K2;
      var $C;
      var $F3;
      $1=$m;
      $2=$tbase;
      $3=$tsize;
      $4=$mmapped;
      var $5=$1;
      var $6=(($5+24)|0);
      var $7=HEAP32[(($6)>>2)];
      var $8=$7;
      $old_top=$8;
      var $9=$1;
      var $10=$old_top;
      var $11=_segment_holding($9, $10);
      $oldsp=$11;
      var $12=$oldsp;
      var $13=(($12)|0);
      var $14=HEAP32[(($13)>>2)];
      var $15=$oldsp;
      var $16=(($15+4)|0);
      var $17=HEAP32[(($16)>>2)];
      var $18=(($14+$17)|0);
      $old_end=$18;
      $ssize=24;
      var $19=$old_end;
      var $20=$ssize;
      var $21=((($20)+(16))|0);
      var $22=((($21)+(7))|0);
      var $23=(((-$22))|0);
      var $24=(($19+$23)|0);
      $rawsp=$24;
      var $25=$rawsp;
      var $26=(($25+8)|0);
      var $27=$26;
      var $28=$27 & 7;
      var $29=(($28)|(0))==0;
      if ($29) { label = 3; break; } else { label = 4; break; }
    case 3: 
      var $39 = 0;label = 5; break;
    case 4: 
      var $32=$rawsp;
      var $33=(($32+8)|0);
      var $34=$33;
      var $35=$34 & 7;
      var $36=(((8)-($35))|0);
      var $37=$36 & 7;
      var $39 = $37;label = 5; break;
    case 5: 
      var $39;
      $offset=$39;
      var $40=$rawsp;
      var $41=$offset;
      var $42=(($40+$41)|0);
      $asp=$42;
      var $43=$asp;
      var $44=$old_top;
      var $45=(($44+16)|0);
      var $46=(($43)>>>(0)) < (($45)>>>(0));
      if ($46) { label = 6; break; } else { label = 7; break; }
    case 6: 
      var $48=$old_top;
      var $52 = $48;label = 8; break;
    case 7: 
      var $50=$asp;
      var $52 = $50;label = 8; break;
    case 8: 
      var $52;
      $csp=$52;
      var $53=$csp;
      var $54=$53;
      $sp=$54;
      var $55=$sp;
      var $56=$55;
      var $57=(($56+8)|0);
      var $58=$57;
      $ss=$58;
      var $59=$sp;
      var $60=$59;
      var $61=$ssize;
      var $62=(($60+$61)|0);
      var $63=$62;
      $tnext=$63;
      var $64=$tnext;
      $p=$64;
      $nfences=0;
      var $65=$1;
      var $66=$2;
      var $67=$66;
      var $68=$3;
      var $69=((($68)-(40))|0);
      _init_top($65, $67, $69);
      var $70=$ssize;
      var $71=$70 | 1;
      var $72=$71 | 2;
      var $73=$sp;
      var $74=(($73+4)|0);
      HEAP32[(($74)>>2)]=$72;
      var $75=$ss;
      var $76=$1;
      var $77=(($76+448)|0);
      var $78=$75;
      var $79=$77;
      assert(16 % 1 === 0);HEAP32[(($78)>>2)]=HEAP32[(($79)>>2)];HEAP32[((($78)+(4))>>2)]=HEAP32[((($79)+(4))>>2)];HEAP32[((($78)+(8))>>2)]=HEAP32[((($79)+(8))>>2)];HEAP32[((($78)+(12))>>2)]=HEAP32[((($79)+(12))>>2)];
      var $80=$2;
      var $81=$1;
      var $82=(($81+448)|0);
      var $83=(($82)|0);
      HEAP32[(($83)>>2)]=$80;
      var $84=$3;
      var $85=$1;
      var $86=(($85+448)|0);
      var $87=(($86+4)|0);
      HEAP32[(($87)>>2)]=$84;
      var $88=$4;
      var $89=$1;
      var $90=(($89+448)|0);
      var $91=(($90+12)|0);
      HEAP32[(($91)>>2)]=$88;
      var $92=$ss;
      var $93=$1;
      var $94=(($93+448)|0);
      var $95=(($94+8)|0);
      HEAP32[(($95)>>2)]=$92;
      label = 9; break;
    case 9: 
      var $97=$p;
      var $98=$97;
      var $99=(($98+4)|0);
      var $100=$99;
      $nextp=$100;
      var $101=$p;
      var $102=(($101+4)|0);
      HEAP32[(($102)>>2)]=7;
      var $103=$nfences;
      var $104=((($103)+(1))|0);
      $nfences=$104;
      var $105=$nextp;
      var $106=(($105+4)|0);
      var $107=$106;
      var $108=$old_end;
      var $109=(($107)>>>(0)) < (($108)>>>(0));
      if ($109) { label = 10; break; } else { label = 11; break; }
    case 10: 
      var $111=$nextp;
      $p=$111;
      label = 12; break;
    case 11: 
      label = 13; break;
    case 12: 
      label = 9; break;
    case 13: 
      var $115=$csp;
      var $116=$old_top;
      var $117=(($115)|(0))!=(($116)|(0));
      if ($117) { label = 14; break; } else { label = 50; break; }
    case 14: 
      var $119=$old_top;
      var $120=$119;
      $q=$120;
      var $121=$csp;
      var $122=$old_top;
      var $123=$121;
      var $124=$122;
      var $125=((($123)-($124))|0);
      $psize=$125;
      var $126=$q;
      var $127=$126;
      var $128=$psize;
      var $129=(($127+$128)|0);
      var $130=$129;
      $tn=$130;
      var $131=$tn;
      var $132=(($131+4)|0);
      var $133=HEAP32[(($132)>>2)];
      var $134=$133 & -2;
      HEAP32[(($132)>>2)]=$134;
      var $135=$psize;
      var $136=$135 | 1;
      var $137=$q;
      var $138=(($137+4)|0);
      HEAP32[(($138)>>2)]=$136;
      var $139=$psize;
      var $140=$q;
      var $141=$140;
      var $142=$psize;
      var $143=(($141+$142)|0);
      var $144=$143;
      var $145=(($144)|0);
      HEAP32[(($145)>>2)]=$139;
      var $146=$psize;
      var $147=$146 >>> 3;
      var $148=(($147)>>>(0)) < 32;
      if ($148) { label = 15; break; } else { label = 22; break; }
    case 15: 
      var $150=$psize;
      var $151=$150 >>> 3;
      $I=$151;
      var $152=$I;
      var $153=$152 << 1;
      var $154=$1;
      var $155=(($154+40)|0);
      var $156=(($155+($153<<2))|0);
      var $157=$156;
      var $158=$157;
      $B=$158;
      var $159=$B;
      $F=$159;
      var $160=$1;
      var $161=(($160)|0);
      var $162=HEAP32[(($161)>>2)];
      var $163=$I;
      var $164=1 << $163;
      var $165=$162 & $164;
      var $166=(($165)|(0))!=0;
      if ($166) { label = 17; break; } else { label = 16; break; }
    case 16: 
      var $168=$I;
      var $169=1 << $168;
      var $170=$1;
      var $171=(($170)|0);
      var $172=HEAP32[(($171)>>2)];
      var $173=$172 | $169;
      HEAP32[(($171)>>2)]=$173;
      label = 21; break;
    case 17: 
      var $175=$B;
      var $176=(($175+8)|0);
      var $177=HEAP32[(($176)>>2)];
      var $178=$177;
      var $179=$1;
      var $180=(($179+16)|0);
      var $181=HEAP32[(($180)>>2)];
      var $182=(($178)>>>(0)) >= (($181)>>>(0));
      var $183=(($182)&(1));
      var $184=($183);
      var $185=(($184)|(0))!=0;
      if ($185) { label = 18; break; } else { label = 19; break; }
    case 18: 
      var $187=$B;
      var $188=(($187+8)|0);
      var $189=HEAP32[(($188)>>2)];
      $F=$189;
      label = 20; break;
    case 19: 
      _abort();
      throw "Reached an unreachable!"
    case 20: 
      label = 21; break;
    case 21: 
      var $193=$q;
      var $194=$B;
      var $195=(($194+8)|0);
      HEAP32[(($195)>>2)]=$193;
      var $196=$q;
      var $197=$F;
      var $198=(($197+12)|0);
      HEAP32[(($198)>>2)]=$196;
      var $199=$F;
      var $200=$q;
      var $201=(($200+8)|0);
      HEAP32[(($201)>>2)]=$199;
      var $202=$B;
      var $203=$q;
      var $204=(($203+12)|0);
      HEAP32[(($204)>>2)]=$202;
      label = 49; break;
    case 22: 
      var $206=$q;
      var $207=$206;
      $TP=$207;
      var $208=$psize;
      var $209=$208 >>> 8;
      $X=$209;
      var $210=$X;
      var $211=(($210)|(0))==0;
      if ($211) { label = 23; break; } else { label = 24; break; }
    case 23: 
      $I1=0;
      label = 28; break;
    case 24: 
      var $214=$X;
      var $215=(($214)>>>(0)) > 65535;
      if ($215) { label = 25; break; } else { label = 26; break; }
    case 25: 
      $I1=31;
      label = 27; break;
    case 26: 
      var $218=$X;
      $Y=$218;
      var $219=$Y;
      var $220=((($219)-(256))|0);
      var $221=$220 >>> 16;
      var $222=$221 & 8;
      $N=$222;
      var $223=$N;
      var $224=$Y;
      var $225=$224 << $223;
      $Y=$225;
      var $226=((($225)-(4096))|0);
      var $227=$226 >>> 16;
      var $228=$227 & 4;
      $K=$228;
      var $229=$K;
      var $230=$N;
      var $231=((($230)+($229))|0);
      $N=$231;
      var $232=$K;
      var $233=$Y;
      var $234=$233 << $232;
      $Y=$234;
      var $235=((($234)-(16384))|0);
      var $236=$235 >>> 16;
      var $237=$236 & 2;
      $K=$237;
      var $238=$N;
      var $239=((($238)+($237))|0);
      $N=$239;
      var $240=$N;
      var $241=(((14)-($240))|0);
      var $242=$K;
      var $243=$Y;
      var $244=$243 << $242;
      $Y=$244;
      var $245=$244 >>> 15;
      var $246=((($241)+($245))|0);
      $K=$246;
      var $247=$K;
      var $248=$247 << 1;
      var $249=$psize;
      var $250=$K;
      var $251=((($250)+(7))|0);
      var $252=$249 >>> (($251)>>>(0));
      var $253=$252 & 1;
      var $254=((($248)+($253))|0);
      $I1=$254;
      label = 27; break;
    case 27: 
      label = 28; break;
    case 28: 
      var $257=$I1;
      var $258=$1;
      var $259=(($258+304)|0);
      var $260=(($259+($257<<2))|0);
      $H=$260;
      var $261=$I1;
      var $262=$TP;
      var $263=(($262+28)|0);
      HEAP32[(($263)>>2)]=$261;
      var $264=$TP;
      var $265=(($264+16)|0);
      var $266=(($265+4)|0);
      HEAP32[(($266)>>2)]=0;
      var $267=$TP;
      var $268=(($267+16)|0);
      var $269=(($268)|0);
      HEAP32[(($269)>>2)]=0;
      var $270=$1;
      var $271=(($270+4)|0);
      var $272=HEAP32[(($271)>>2)];
      var $273=$I1;
      var $274=1 << $273;
      var $275=$272 & $274;
      var $276=(($275)|(0))!=0;
      if ($276) { label = 30; break; } else { label = 29; break; }
    case 29: 
      var $278=$I1;
      var $279=1 << $278;
      var $280=$1;
      var $281=(($280+4)|0);
      var $282=HEAP32[(($281)>>2)];
      var $283=$282 | $279;
      HEAP32[(($281)>>2)]=$283;
      var $284=$TP;
      var $285=$H;
      HEAP32[(($285)>>2)]=$284;
      var $286=$H;
      var $287=$286;
      var $288=$TP;
      var $289=(($288+24)|0);
      HEAP32[(($289)>>2)]=$287;
      var $290=$TP;
      var $291=$TP;
      var $292=(($291+12)|0);
      HEAP32[(($292)>>2)]=$290;
      var $293=$TP;
      var $294=(($293+8)|0);
      HEAP32[(($294)>>2)]=$290;
      label = 48; break;
    case 30: 
      var $296=$H;
      var $297=HEAP32[(($296)>>2)];
      $T=$297;
      var $298=$psize;
      var $299=$I1;
      var $300=(($299)|(0))==31;
      if ($300) { label = 31; break; } else { label = 32; break; }
    case 31: 
      var $309 = 0;label = 33; break;
    case 32: 
      var $303=$I1;
      var $304=$303 >>> 1;
      var $305=((($304)+(8))|0);
      var $306=((($305)-(2))|0);
      var $307=(((31)-($306))|0);
      var $309 = $307;label = 33; break;
    case 33: 
      var $309;
      var $310=$298 << $309;
      $K2=$310;
      label = 34; break;
    case 34: 
      var $312=$T;
      var $313=(($312+4)|0);
      var $314=HEAP32[(($313)>>2)];
      var $315=$314 & -8;
      var $316=$psize;
      var $317=(($315)|(0))!=(($316)|(0));
      if ($317) { label = 35; break; } else { label = 41; break; }
    case 35: 
      var $319=$K2;
      var $320=$319 >>> 31;
      var $321=$320 & 1;
      var $322=$T;
      var $323=(($322+16)|0);
      var $324=(($323+($321<<2))|0);
      $C=$324;
      var $325=$K2;
      var $326=$325 << 1;
      $K2=$326;
      var $327=$C;
      var $328=HEAP32[(($327)>>2)];
      var $329=(($328)|(0))!=0;
      if ($329) { label = 36; break; } else { label = 37; break; }
    case 36: 
      var $331=$C;
      var $332=HEAP32[(($331)>>2)];
      $T=$332;
      label = 40; break;
    case 37: 
      var $334=$C;
      var $335=$334;
      var $336=$1;
      var $337=(($336+16)|0);
      var $338=HEAP32[(($337)>>2)];
      var $339=(($335)>>>(0)) >= (($338)>>>(0));
      var $340=(($339)&(1));
      var $341=($340);
      var $342=(($341)|(0))!=0;
      if ($342) { label = 38; break; } else { label = 39; break; }
    case 38: 
      var $344=$TP;
      var $345=$C;
      HEAP32[(($345)>>2)]=$344;
      var $346=$T;
      var $347=$TP;
      var $348=(($347+24)|0);
      HEAP32[(($348)>>2)]=$346;
      var $349=$TP;
      var $350=$TP;
      var $351=(($350+12)|0);
      HEAP32[(($351)>>2)]=$349;
      var $352=$TP;
      var $353=(($352+8)|0);
      HEAP32[(($353)>>2)]=$349;
      label = 47; break;
    case 39: 
      _abort();
      throw "Reached an unreachable!"
    case 40: 
      label = 46; break;
    case 41: 
      var $357=$T;
      var $358=(($357+8)|0);
      var $359=HEAP32[(($358)>>2)];
      $F3=$359;
      var $360=$T;
      var $361=$360;
      var $362=$1;
      var $363=(($362+16)|0);
      var $364=HEAP32[(($363)>>2)];
      var $365=(($361)>>>(0)) >= (($364)>>>(0));
      if ($365) { label = 42; break; } else { var $374 = 0;label = 43; break; }
    case 42: 
      var $367=$F3;
      var $368=$367;
      var $369=$1;
      var $370=(($369+16)|0);
      var $371=HEAP32[(($370)>>2)];
      var $372=(($368)>>>(0)) >= (($371)>>>(0));
      var $374 = $372;label = 43; break;
    case 43: 
      var $374;
      var $375=(($374)&(1));
      var $376=($375);
      var $377=(($376)|(0))!=0;
      if ($377) { label = 44; break; } else { label = 45; break; }
    case 44: 
      var $379=$TP;
      var $380=$F3;
      var $381=(($380+12)|0);
      HEAP32[(($381)>>2)]=$379;
      var $382=$T;
      var $383=(($382+8)|0);
      HEAP32[(($383)>>2)]=$379;
      var $384=$F3;
      var $385=$TP;
      var $386=(($385+8)|0);
      HEAP32[(($386)>>2)]=$384;
      var $387=$T;
      var $388=$TP;
      var $389=(($388+12)|0);
      HEAP32[(($389)>>2)]=$387;
      var $390=$TP;
      var $391=(($390+24)|0);
      HEAP32[(($391)>>2)]=0;
      label = 47; break;
    case 45: 
      _abort();
      throw "Reached an unreachable!"
    case 46: 
      label = 34; break;
    case 47: 
      label = 48; break;
    case 48: 
      label = 49; break;
    case 49: 
      label = 50; break;
    case 50: 
      return;
    default: assert(0, "bad label: " + label);
  }
}
function _strtod($string, $endPtr) {
  var label = 0;
  label = 2; 
  while(1) switch(label) {
    case 2: 
      var $1;
      var $2;
      var $3;
      var $sign;
      var $expSign;
      var $fraction;
      var $dblExp;
      var $d;
      var $p;
      var $c;
      var $exp;
      var $fracExp;
      var $mantSize;
      var $decPt;
      var $pExp;
      var $frac1;
      var $frac2;
      $2=$string;
      $3=$endPtr;
      $expSign=0;
      $exp=0;
      $fracExp=0;
      var $4=$2;
      $p=$4;
      label = 3; break;
    case 3: 
      var $6=$p;
      var $7=HEAP8[($6)];
      var $8=(($7 << 24) >> 24);
      var $9=_isspace($8);
      var $10=(($9)|(0))!=0;
      if ($10) { label = 4; break; } else { label = 5; break; }
    case 4: 
      var $12=$p;
      var $13=(($12+1)|0);
      $p=$13;
      label = 3; break;
    case 5: 
      var $15=$p;
      var $16=HEAP8[($15)];
      var $17=(($16 << 24) >> 24);
      var $18=(($17)|(0))==45;
      if ($18) { label = 6; break; } else { label = 7; break; }
    case 6: 
      $sign=1;
      var $20=$p;
      var $21=(($20+1)|0);
      $p=$21;
      label = 10; break;
    case 7: 
      var $23=$p;
      var $24=HEAP8[($23)];
      var $25=(($24 << 24) >> 24);
      var $26=(($25)|(0))==43;
      if ($26) { label = 8; break; } else { label = 9; break; }
    case 8: 
      var $28=$p;
      var $29=(($28+1)|0);
      $p=$29;
      label = 9; break;
    case 9: 
      $sign=0;
      label = 10; break;
    case 10: 
      $decPt=-1;
      $mantSize=0;
      label = 11; break;
    case 11: 
      var $33=$p;
      var $34=HEAP8[($33)];
      var $35=(($34 << 24) >> 24);
      $c=$35;
      var $36=$c;
      var $37=_isdigit($36);
      var $38=(($37)|(0))!=0;
      if ($38) { label = 16; break; } else { label = 12; break; }
    case 12: 
      var $40=$c;
      var $41=(($40)|(0))!=46;
      if ($41) { label = 14; break; } else { label = 13; break; }
    case 13: 
      var $43=$decPt;
      var $44=(($43)|(0)) >= 0;
      if ($44) { label = 14; break; } else { label = 15; break; }
    case 14: 
      label = 18; break;
    case 15: 
      var $47=$mantSize;
      $decPt=$47;
      label = 16; break;
    case 16: 
      var $49=$p;
      var $50=(($49+1)|0);
      $p=$50;
      label = 17; break;
    case 17: 
      var $52=$mantSize;
      var $53=((($52)+(1))|0);
      $mantSize=$53;
      label = 11; break;
    case 18: 
      var $55=$p;
      $pExp=$55;
      var $56=$mantSize;
      var $57=$p;
      var $58=(((-$56))|0);
      var $59=(($57+$58)|0);
      $p=$59;
      var $60=$decPt;
      var $61=(($60)|(0)) < 0;
      if ($61) { label = 19; break; } else { label = 20; break; }
    case 19: 
      var $63=$mantSize;
      $decPt=$63;
      label = 21; break;
    case 20: 
      var $65=$mantSize;
      var $66=((($65)-(1))|0);
      $mantSize=$66;
      label = 21; break;
    case 21: 
      var $68=$mantSize;
      var $69=(($68)|(0)) > 18;
      if ($69) { label = 22; break; } else { label = 23; break; }
    case 22: 
      var $71=$decPt;
      var $72=((($71)-(18))|0);
      $fracExp=$72;
      $mantSize=18;
      label = 24; break;
    case 23: 
      var $74=$decPt;
      var $75=$mantSize;
      var $76=((($74)-($75))|0);
      $fracExp=$76;
      label = 24; break;
    case 24: 
      var $78=$mantSize;
      var $79=(($78)|(0))==0;
      if ($79) { label = 25; break; } else { label = 26; break; }
    case 25: 
      $fraction=0;
      var $81=$2;
      $p=$81;
      label = 68; break;
    case 26: 
      $frac1=0;
      label = 27; break;
    case 27: 
      var $84=$mantSize;
      var $85=(($84)|(0)) > 9;
      if ($85) { label = 28; break; } else { label = 32; break; }
    case 28: 
      var $87=$p;
      var $88=HEAP8[($87)];
      var $89=(($88 << 24) >> 24);
      $c=$89;
      var $90=$p;
      var $91=(($90+1)|0);
      $p=$91;
      var $92=$c;
      var $93=(($92)|(0))==46;
      if ($93) { label = 29; break; } else { label = 30; break; }
    case 29: 
      var $95=$p;
      var $96=HEAP8[($95)];
      var $97=(($96 << 24) >> 24);
      $c=$97;
      var $98=$p;
      var $99=(($98+1)|0);
      $p=$99;
      label = 30; break;
    case 30: 
      var $101=$frac1;
      var $102=((($101)*(10))&-1);
      var $103=$c;
      var $104=((($103)-(48))|0);
      var $105=((($102)+($104))|0);
      $frac1=$105;
      label = 31; break;
    case 31: 
      var $107=$mantSize;
      var $108=((($107)-(1))|0);
      $mantSize=$108;
      label = 27; break;
    case 32: 
      $frac2=0;
      label = 33; break;
    case 33: 
      var $111=$mantSize;
      var $112=(($111)|(0)) > 0;
      if ($112) { label = 34; break; } else { label = 38; break; }
    case 34: 
      var $114=$p;
      var $115=HEAP8[($114)];
      var $116=(($115 << 24) >> 24);
      $c=$116;
      var $117=$p;
      var $118=(($117+1)|0);
      $p=$118;
      var $119=$c;
      var $120=(($119)|(0))==46;
      if ($120) { label = 35; break; } else { label = 36; break; }
    case 35: 
      var $122=$p;
      var $123=HEAP8[($122)];
      var $124=(($123 << 24) >> 24);
      $c=$124;
      var $125=$p;
      var $126=(($125+1)|0);
      $p=$126;
      label = 36; break;
    case 36: 
      var $128=$frac2;
      var $129=((($128)*(10))&-1);
      var $130=$c;
      var $131=((($130)-(48))|0);
      var $132=((($129)+($131))|0);
      $frac2=$132;
      label = 37; break;
    case 37: 
      var $134=$mantSize;
      var $135=((($134)-(1))|0);
      $mantSize=$135;
      label = 33; break;
    case 38: 
      var $137=$frac1;
      var $138=(($137)|(0));
      var $139=($138)*(1000000000);
      var $140=$frac2;
      var $141=(($140)|(0));
      var $142=($139)+($141);
      $fraction=$142;
      label = 39; break;
    case 39: 
      var $144=$pExp;
      $p=$144;
      var $145=$p;
      var $146=HEAP8[($145)];
      var $147=(($146 << 24) >> 24);
      var $148=(($147)|(0))==69;
      if ($148) { label = 41; break; } else { label = 40; break; }
    case 40: 
      var $150=$p;
      var $151=HEAP8[($150)];
      var $152=(($151 << 24) >> 24);
      var $153=(($152)|(0))==101;
      if ($153) { label = 41; break; } else { label = 50; break; }
    case 41: 
      var $155=$p;
      var $156=(($155+1)|0);
      $p=$156;
      var $157=$p;
      var $158=HEAP8[($157)];
      var $159=(($158 << 24) >> 24);
      var $160=(($159)|(0))==45;
      if ($160) { label = 42; break; } else { label = 43; break; }
    case 42: 
      $expSign=1;
      var $162=$p;
      var $163=(($162+1)|0);
      $p=$163;
      label = 46; break;
    case 43: 
      var $165=$p;
      var $166=HEAP8[($165)];
      var $167=(($166 << 24) >> 24);
      var $168=(($167)|(0))==43;
      if ($168) { label = 44; break; } else { label = 45; break; }
    case 44: 
      var $170=$p;
      var $171=(($170+1)|0);
      $p=$171;
      label = 45; break;
    case 45: 
      $expSign=0;
      label = 46; break;
    case 46: 
      label = 47; break;
    case 47: 
      var $175=$p;
      var $176=HEAP8[($175)];
      var $177=(($176 << 24) >> 24);
      var $178=_isdigit($177);
      var $179=(($178)|(0))!=0;
      if ($179) { label = 48; break; } else { label = 49; break; }
    case 48: 
      var $181=$exp;
      var $182=((($181)*(10))&-1);
      var $183=$p;
      var $184=HEAP8[($183)];
      var $185=(($184 << 24) >> 24);
      var $186=((($185)-(48))|0);
      var $187=((($182)+($186))|0);
      $exp=$187;
      var $188=$p;
      var $189=(($188+1)|0);
      $p=$189;
      label = 47; break;
    case 49: 
      label = 50; break;
    case 50: 
      var $192=$expSign;
      var $193=(($192)|(0))!=0;
      if ($193) { label = 51; break; } else { label = 52; break; }
    case 51: 
      var $195=$fracExp;
      var $196=$exp;
      var $197=((($195)-($196))|0);
      $exp=$197;
      label = 53; break;
    case 52: 
      var $199=$fracExp;
      var $200=$exp;
      var $201=((($199)+($200))|0);
      $exp=$201;
      label = 53; break;
    case 53: 
      var $203=$exp;
      var $204=(($203)|(0)) < 0;
      if ($204) { label = 54; break; } else { label = 55; break; }
    case 54: 
      $expSign=1;
      var $206=$exp;
      var $207=(((-$206))|0);
      $exp=$207;
      label = 56; break;
    case 55: 
      $expSign=0;
      label = 56; break;
    case 56: 
      var $210=$exp;
      var $211=HEAP32[((5242980)>>2)];
      var $212=(($210)|(0)) > (($211)|(0));
      if ($212) { label = 57; break; } else { label = 58; break; }
    case 57: 
      var $214=HEAP32[((5242980)>>2)];
      $exp=$214;
      var $215=___errno_location();
      HEAP32[(($215)>>2)]=34;
      label = 58; break;
    case 58: 
      $dblExp=1;
      $d=((5242880)|0);
      label = 59; break;
    case 59: 
      var $218=$exp;
      var $219=(($218)|(0))!=0;
      if ($219) { label = 60; break; } else { label = 64; break; }
    case 60: 
      var $221=$exp;
      var $222=$221 & 1;
      var $223=(($222)|(0))!=0;
      if ($223) { label = 61; break; } else { label = 62; break; }
    case 61: 
      var $225=$d;
      var $226=(HEAP32[((tempDoublePtr)>>2)]=HEAP32[(($225)>>2)],HEAP32[(((tempDoublePtr)+(4))>>2)]=HEAP32[((($225)+(4))>>2)],HEAPF64[(tempDoublePtr)>>3]);
      var $227=$dblExp;
      var $228=($227)*($226);
      $dblExp=$228;
      label = 62; break;
    case 62: 
      label = 63; break;
    case 63: 
      var $231=$exp;
      var $232=$231 >> 1;
      $exp=$232;
      var $233=$d;
      var $234=(($233+8)|0);
      $d=$234;
      label = 59; break;
    case 64: 
      var $236=$expSign;
      var $237=(($236)|(0))!=0;
      if ($237) { label = 65; break; } else { label = 66; break; }
    case 65: 
      var $239=$dblExp;
      var $240=$fraction;
      var $241=($240)/($239);
      $fraction=$241;
      label = 67; break;
    case 66: 
      var $243=$dblExp;
      var $244=$fraction;
      var $245=($244)*($243);
      $fraction=$245;
      label = 67; break;
    case 67: 
      label = 68; break;
    case 68: 
      var $248=$3;
      var $249=(($248)|(0))!=0;
      if ($249) { label = 69; break; } else { label = 70; break; }
    case 69: 
      var $251=$p;
      var $252=$3;
      HEAP32[(($252)>>2)]=$251;
      label = 70; break;
    case 70: 
      var $254=$sign;
      var $255=(($254)|(0))!=0;
      if ($255) { label = 71; break; } else { label = 72; break; }
    case 71: 
      var $257=$fraction;
      var $258=(-$257);
      $1=$258;
      label = 73; break;
    case 72: 
      var $260=$fraction;
      $1=$260;
      label = 73; break;
    case 73: 
      var $262=$1;
      return $262;
    default: assert(0, "bad label: " + label);
  }
}
function _atof($str) {
  var label = 0;
  var $1;
  $1=$str;
  var $2=$1;
  var $3=_strtod($2, 0);
  return $3;
}
// EMSCRIPTEN_END_FUNCS
// EMSCRIPTEN_END_FUNCS
// Warning: printing of i64 values may be slightly rounded! No deep i64 math used, so precise i64 code not included
var i64Math = null;
// === Auto-generated postamble setup entry stuff ===
Module.callMain = function callMain(args) {
  var argc = args.length+1;
  function pad() {
    for (var i = 0; i < 4-1; i++) {
      argv.push(0);
    }
  }
  var argv = [allocate(intArrayFromString("/bin/this.program"), 'i8', ALLOC_STATIC) ];
  pad();
  for (var i = 0; i < argc-1; i = i + 1) {
    argv.push(allocate(intArrayFromString(args[i]), 'i8', ALLOC_STATIC));
    pad();
  }
  argv.push(0);
  argv = allocate(argv, 'i32', ALLOC_STATIC);
  var ret;
  var initialStackTop = STACKTOP;
  try {
    ret = Module['_main'](argc, argv, 0);
  }
  catch(e) {
    if (e.name == 'ExitStatus') {
      return e.status;
    } else if (e == 'SimulateInfiniteLoop') {
      Module['noExitRuntime'] = true;
    } else {
      throw e;
    }
  } finally {
    STACKTOP = initialStackTop;
  }
  return ret;
}
function run(args) {
  args = args || Module['arguments'];
  if (runDependencies > 0) {
    Module.printErr('run() called, but dependencies remain, so not running');
    return 0;
  }
  if (Module['preRun']) {
    if (typeof Module['preRun'] == 'function') Module['preRun'] = [Module['preRun']];
    var toRun = Module['preRun'];
    Module['preRun'] = [];
    for (var i = toRun.length-1; i >= 0; i--) {
      toRun[i]();
    }
    if (runDependencies > 0) {
      // a preRun added a dependency, run will be called later
      return 0;
    }
  }
  function doRun() {
    var ret = 0;
    calledRun = true;
    if (Module['_main']) {
      preMain();
      ret = Module.callMain(args);
      if (!Module['noExitRuntime']) {
        exitRuntime();
      }
    }
    if (Module['postRun']) {
      if (typeof Module['postRun'] == 'function') Module['postRun'] = [Module['postRun']];
      while (Module['postRun'].length > 0) {
        Module['postRun'].pop()();
      }
    }
    return ret;
  }
  if (Module['setStatus']) {
    Module['setStatus']('Running...');
    setTimeout(function() {
      setTimeout(function() {
        Module['setStatus']('');
      }, 1);
      doRun();
    }, 1);
    return 0;
  } else {
    return doRun();
  }
}
Module['run'] = Module.run = run;
// {{PRE_RUN_ADDITIONS}}
if (Module['preInit']) {
  if (typeof Module['preInit'] == 'function') Module['preInit'] = [Module['preInit']];
  while (Module['preInit'].length > 0) {
    Module['preInit'].pop()();
  }
}
initRuntime();
var shouldRunNow = true;
if (Module['noInitialRun']) {
  shouldRunNow = false;
}
if (shouldRunNow) {
  run();
}
// {{POST_RUN_ADDITIONS}}
  // {{MODULE_ADDITIONS}}
