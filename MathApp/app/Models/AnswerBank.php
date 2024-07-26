<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AnswerBank extends Model
{
    use HasFactory;

    protected $table = 'answer_banks';
    protected $primaryKey = 'answerBankID';
    
    protected $fillable = [
        'challengeNo',
        'questionBankID',
        'answerBank',
    ];

    public function challenge()
    {
        return $this->belongsTo(Challenge::class, 'challengeNo', 'challengeNo');
    }

    public function question_bank()
    {
        return $this->belongsTo(QuestionBank::class, 'questionBankID', 'questionBankID');
    }
}