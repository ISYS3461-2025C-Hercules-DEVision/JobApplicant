export function zodToFieldErrors(zodError) {
    const out = {};
    for (const issue of zodError?.issues || []) {
        const key = issue.path?.join(".") || "_form";
        if (!out[key]) out[key] = issue.message;
    }
    return out;
}