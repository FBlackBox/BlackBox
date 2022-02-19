#define FUNCTION(name) \
.data; \
.align 4; \
.global name; \
name:

#define VAR(name) \
.global name; \
name:

FUNCTION(pine_direct_jump_trampoline)
ldr pc, pine_direct_jump_trampoline_jump_entry
VAR(pine_direct_jump_trampoline_jump_entry)
.long 0

FUNCTION(pine_bridge_jump_trampoline)
ldr ip, pine_bridge_jump_trampoline_target_method
cmp r0, ip
ldrne pc, pine_bridge_jump_trampoline_call_origin_entry
ldr ip, pine_bridge_jump_trampoline_extras

acquire_lock:
ldrex r0, [ip]
cmp r0, #1
wfene // other thread holding the lock, wait it release lock
mov r0, #0
strexeq r0, r0, [ip]
cmpeq r0, #0 // store succeeded?
bne acquire_lock // acquire lock failed, try again
dmb

// Now we hold the lock!
str r1, [ip, #4]
str r2, [ip, #8]
str r3, [ip, #12]
ldr r1, pine_bridge_jump_trampoline_target_method // first param = callee ArtMethod
mov r2, ip // second param = extras (saved r1, r2, r3)
mov r3, sp // third param = sp
ldr r0, pine_bridge_jump_trampoline_bridge_method
ldr pc, pine_bridge_jump_trampoline_bridge_entry
VAR(pine_bridge_jump_trampoline_target_method)
.long 0
VAR(pine_bridge_jump_trampoline_extras)
.long 0
VAR(pine_bridge_jump_trampoline_bridge_method)
.long 0
VAR(pine_bridge_jump_trampoline_bridge_entry)
.long 0
VAR(pine_bridge_jump_trampoline_call_origin_entry)
.long 0

FUNCTION(pine_call_origin_trampoline)
ldr r0, pine_call_origin_trampoline_origin_method
ldr pc, pine_call_origin_trampoline_origin_code_entry
VAR(pine_call_origin_trampoline_origin_method)
.long 0
VAR(pine_call_origin_trampoline_origin_code_entry)
.long 0

FUNCTION(pine_backup_trampoline)
ldr r0, pine_backup_trampoline_origin_method
VAR(pine_backup_trampoline_override_space)
.long 0 // 4 bytes (will be overwritten)
.long 0 // 4 bytes (will be overwritten)
nop // 4 bytes, may be overwritten for anti checks
ldr pc, pine_backup_trampoline_remaining_code_entry
VAR(pine_backup_trampoline_origin_method)
.long 0
VAR(pine_backup_trampoline_remaining_code_entry)
.long 0

FUNCTION(pine_trampolines_end)
// End tag used for calculate size only
