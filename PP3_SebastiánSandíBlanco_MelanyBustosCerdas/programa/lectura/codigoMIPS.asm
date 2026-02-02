.data
    _g1_: .word 5

.text
.globl main


multiplicar:
    # Prólogo de función
    subu $sp, $sp, 32
    sw $ra, 28($sp)
    sw $fp, 24($sp)
    move $fp, $sp
    # Parámetro _a_
    sw $a0, 20($fp)
    # Parámetro _b_
    sw $a1, 16($fp)
    # t1 = _a_ * _b_
    lw $t0, 20($fp)
    lw $t1, 16($fp)
    mul $t2, $t0, $t1
    sw $t2, 8($fp)
    # Asignación: _resM_ = t1
    lw $t0, 8($fp)
    sw $t0, 4($fp)
    # Return
    lw $v0, 4($fp)
    j multiplicar_end
multiplicar_end:
    # Epílogo de función
    move $sp, $fp
    lw $fp, 24($sp)
    lw $ra, 28($sp)
    addu $sp, $sp, 32
    jr $ra


operar:
    # Prólogo de función
    subu $sp, $sp, 32
    sw $ra, 28($sp)
    sw $fp, 24($sp)
    move $fp, $sp
    # Parámetro _n1_
    sw $a0, 20($fp)
    # Parámetro _n2_
    sw $a1, 16($fp)
    # Asignación: _temp_ = 0
    li $t0, 0
    sw $t0, 8($fp)
    # t4 = _n1_ > _n2_
    lw $t0, 20($fp)
    lw $t1, 16($fp)
    sgt $t2, $t0, $t1
    sw $t2, 4($fp)
    lw $t0, 4($fp)
    beq $t0, $zero, L0
    # Argumento 0
    lw $a0, 20($fp)
    # Argumento 1
    lw $a1, _g1_
    # Llamada a función multiplicar
    jal multiplicar
    sw $v0, 0($fp)
    j endF
L0:
    # t6 = _n1_ > _n2_
    lw $t0, 20($fp)
    lw $t1, 16($fp)
    sgt $t2, $t0, $t1
    sw $t2, -4($fp)
    lw $t0, -4($fp)
    beq $t0, $zero, L1
    # Argumento 0
    lw $a0, 20($fp)
    # Argumento 1
    lw $a1, _g1_
    # Llamada a función multiplicar
    jal multiplicar
    sw $v0, -8($fp)
    j endF
L1:
else:
endF:
    # Return
    lw $v0, 8($fp)
    j operar_end
operar_end:
    # Epílogo de función
    move $sp, $fp
    lw $fp, 24($sp)
    lw $ra, 28($sp)
    addu $sp, $sp, 32
    jr $ra

main:
    # Inicio del programa principal
    subu $sp, $sp, 32
    move $fp, $sp
    # Asignación: _x_ = 10
    li $t0, 10
    sw $t0, 24($fp)
    # Asignación: _y_ = 20
    li $t0, 20
    sw $t0, 20($fp)
    # Argumento 0
    lw $a0, 24($fp)
    # Argumento 1
    lw $a1, 20($fp)
    # Llamada a función operar
    jal operar
    sw $v0, 16($fp)
    # Asignación: _final_ = t10
    lw $t0, 16($fp)
    sw $t0, 12($fp)
    # Finalizar programa
    li $v0, 10
    syscall

