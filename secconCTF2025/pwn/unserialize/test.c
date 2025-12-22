#include<stdio.h>
#include<stdlib.h>

int main(){
  // const char *szbuf= "4294967297"; // 2^32+1 
  const char *szbuf= "04294967297"; // 2^32+1 
  // const char *szbuf= "032";  
  // const char *szbuf= "039";  
  
  printf("the analysis of : %s\n", szbuf);
  printf("------------------------\n");

  //atoi的转换是基于int类型的，因此溢出也会基于int类型解决，从而下面应该输出1 
  int buf_atoi = atoi(szbuf);
  printf("atoi(szbuf)=%d\n", buf_atoi);

  //如果指定转换为long类型，使用strtoul，那么就会基于开头是否有0决定8进制，直到9停止 
  //"032"(8进制) -> 26(10进制)
  //"0x32"(16进制) -> 50(10进制)
  unsigned long auto_toul_buf = strtoul(szbuf, NULL, 0);
  printf("strtoul(szbuf,NULL,0)=%lu\n", auto_toul_buf);

  // strtoul指定10进制 
  unsigned long dec_toul_buf= strtoul(szbuf, NULL, 10);
  printf("strtoul(szbuf,NULL,10)=%lu\n", dec_toul_buf);
  return 0;
}
