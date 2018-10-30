var a int
var i int = 0
var s
var list [15] int

func main() {
  var local int
  var local2 int = 10
  var local3
  var local4 [15] int

  for i =0 ; i<15 ; i++ {
    list[i]=i
  }

  i=0

  for i<15 {
    myprint(list[i])
  }

  myfunc(local, local2)
  main2()
}
func myfunc(a int, b) (int, ){
  return a, b
}
func myprint(a int)  {
  fmt.print(a)
}
func mycmp(a int, b int) int {
  if a>b {
      return a
   }
  if a=b {
    return 0
  } else {
    return b
  }
}

func main2()  {
  var temp int = 10
  (temp)
  ++temp
  temp/temp
  temp+temp
  temp == temp
  temp = temp
  return
}

