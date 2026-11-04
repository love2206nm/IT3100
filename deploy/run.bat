@echo off
title Logic Expression Normalizer
echo ========================================================
echo  KHOI DONG UNG DUNG LOGIC EXPRESSION NORMALIZER (IT3100)
echo ========================================================
echo.

if not exist LogicNormalizer.jar (
    echo [THONG BAO] Chua tim thay file LogicNormalizer.jar trong thu muc deploy.
    echo Vui long xem huong dan bien dich va dong goi tai: docs/task/TASK_8_DEPLOYMENT.md
    echo.
    pause
    exit /b
)

java -jar LogicNormalizer.jar
if %errorlevel% neq 0 (
    echo.
    echo [LOI] Khong the chay ung dung. Vui long kiem tra xem may da cai Java (JDK/JRE) chua.
    pause
)
