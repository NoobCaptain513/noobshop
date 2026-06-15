import fs from 'node:fs';

const productSqlPath = process.argv[2];

if (!productSqlPath) {
  throw new Error('Usage: node update_product_category.mjs <product.sql>');
}

const categoryRules = [
  { id: 102, pattern: /手机壳|保护套|手机保护壳|超薄透明壳|全包防摔壳|数据线|充电头|无线耳机|移动电源|读卡器|蓝牙音箱/ },
  { id: 103, pattern: /收纳盒|储物篮|杯垫|挂钩|桌面整理架/ },
  { id: 104, pattern: /宠物零食|每日坚果|曲奇|饼干|饮料|食品/ },
  { id: 105, pattern: /折叠水壶|运动水壶|水壶|骑行头盔|健身手套|跑步手套|遮阳帽|棒球帽|瑜伽垫/ },
  { id: 106, pattern: /护手霜|卸妆水|面膜|口红|保湿乳|防晒霜/ },
  { id: 107, pattern: /便利贴|记事本|文件夹|荧光笔|钢笔|桌面台历/ },
  { id: 108, pattern: /密封罐|饺子模具|铲|保鲜盒|切菜板|隔热手套/ },
  { id: 109, pattern: /手办模型|积木套装|益智玩具|遥控车|拼图|魔方|猫玩具/ },
  { id: 110, pattern: /丝巾|钱包|发夹|帆布袋/ },
  { id: 101, pattern: /防滑垫|狗绳|宠物梳|宠物窝|猫粮盆|宠物用品/ },
];

const content = fs.readFileSync(productSqlPath, 'utf8');
const counts = new Map();
const unmatched = [];
let rowCount = 0;

const nextContent = content.replace(
  /^\((\d+),\s*(\d+),\s*'([^']*)'/gm,
  (match, id, oldCategoryId, name) => {
    rowCount += 1;
    const rule = categoryRules.find((item) => item.pattern.test(name));

    if (!rule) {
      unmatched.push(name);
      return match;
    }

    counts.set(rule.id, (counts.get(rule.id) || 0) + 1);
    return `(${id}, ${rule.id}, '${name}'`;
  },
);

if (unmatched.length > 0) {
  throw new Error(`Found ${unmatched.length} unmatched products: ${unmatched.join(', ')}`);
}

fs.writeFileSync(productSqlPath, nextContent, 'utf8');

console.log(`Updated ${rowCount} product rows in ${productSqlPath}`);
for (const [categoryId, count] of [...counts.entries()].sort((a, b) => a[0] - b[0])) {
  console.log(`category_id=${categoryId} count=${count}`);
}
