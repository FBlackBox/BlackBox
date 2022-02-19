#define FUNCTION(name) \
.syntax unified; \
.align 4; \
.thumb; \
.thumb_func; \
.global name; \
name:

#define VAR(name) \
.global name; \
name:

FUNCTION(pine_thumb_direct_jump_trampoline)
ldr pc, pine_thumb_direct_jump_trampoline_jump_entry
VAR(pine_thumb_direct_jump_trampoline_jump_entry)
.long 0

FUNCTION(pine_thumb_bridge_jump_trampoline)
ldr ip, pine_thumb_bridge_jump_trampoline_target_method
cmp r0, ip

IT NE
ldrne pc, pine_thumb_bridge_jump_trampoline_call_origin_entry

ldr ip, pine_thumb_bridge_jump_trampoline_extras

acquire_lock:
ldrex r0, [ip]
cmp r0, #1

IT NE
wfene // other thread holding the lock, wait it release lock

mov r0, #0

IT EQ
strexeq r0, r0, [ip]

IT EQ
cmpeq r0, #0 // store succeeded?

bne acquire_lock // acquire lock failed, try again

dmb

// Now we hold the lock!
str r1, [ip, #4]
str r2, [ip, #8]
str r3, [ip, #12]
vstr.64 d0, [ip, #16]
vstr.64 d1, [ip, #24]
vstr.64 d2, [ip, #32]
vstr.64 d3, [ip, #40]
vstr.64 d4, [ip, #48]
vstr.64 d5, [ip, #56]
vstr.64 d6, [ip, #64]
vstr.64 d7, [ip, #72]
ldr r1, pine_thumb_bridge_jump_trampoline_target_method // first param = callee ArtMethod
mov r2, ip // second param = extras (saved r1, r2, r3)
mov r3, sp // third param = sp
ldr r0, pine_thumb_bridge_jump_trampoline_bridge_method
ldr pc, pine_thumb_bridge_jump_trampoline_bridge_entry
nop // Alignment padding (unreachable)
VAR(pine_thumb_bridge_jump_trampoline_target_method)
.long 0
VAR(pine_thumb_bridge_jump_trampoline_extras)
.long 0
VAR(pine_thumb_bridge_jump_trampoline_bridge_method)
.long 0
VAR(pine_thumb_bridge_jump_trampoline_bridge_entry)
.long 0
VAR(pine_thumb_bridge_jump_trampoline_call_origin_entry)
.long 0

FUNCTION(pine_thumb_call_origin_trampoline)
ldr r0, pine_thumb_call_origin_trampoline_origin_method
ldr pc, pine_thumb_call_origin_trampoline_origin_code_entry
VAR(pine_thumb_call_origin_trampoline_origin_method)
.long 0
VAR(pine_thumb_call_origin_trampoline_origin_code_entry)
.long 0

FUNCTION(pine_thumb_backup_trampoline)
ldr r0, pine_thumb_backup_trampoline_origin_method
VAR(pine_thumb_backup_trampoline_override_space)
.long 0 // 4 bytes (will be overwritten)
.long 0 // 4 bytes (will be overwritten)
nop // 2 bytes, may be overwritten for anti checks
nop // 2 bytes, may be overwritten for anti checks
nop // 2 bytes (may be overwritten)
ldr pc, pine_thumb_backup_trampoline_remaining_code_entry
VAR(pine_thumb_backup_trampoline_origin_method)
.long 0
VAR(pine_thumb_backup_trampoline_remaining_code_entry)
.long 0

FUNCTION(pine_thumb_trampolines_end)
// End tag used for calculate size only
